package ru.dargen.evoplus.mixin.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.brigadier.ParseResults;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.chat.ChatSendEvent;
import ru.dargen.evoplus.event.chat.CommandEvent;
import ru.dargen.evoplus.event.chat.TitleEvent;
import ru.dargen.evoplus.event.inventory.InventoryCloseEvent;
import ru.dargen.evoplus.event.inventory.InventoryFillEvent;
import ru.dargen.evoplus.event.inventory.InventoryOpenEvent;
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.event.network.ChangeServerEvent;
import ru.dargen.evoplus.event.world.ChunkLoadEvent;
import ru.dargen.evoplus.event.world.ParticleEvent;
import ru.dargen.evoplus.event.world.WorldMapEvent;
import ru.dargen.evoplus.features.misc.RenderFeature;
import ru.dargen.evoplus.util.minecraft.Inventories;
import ru.dargen.evoplus.util.minecraft.TextKt;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static ru.dargen.evoplus.util.minecraft.MinecraftKt.sendPacket;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

//    @Shadow
//    @Final
//    private MinecraftClient client;
    @Shadow
    private LastSeenMessagesCollector lastSeenMessagesCollector;
    @Shadow
    private MessageChain.Packer messagePacker;


//    @Shadow protected abstract void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status packStatus);

//    @Shadow protected abstract ParseResults<CommandSource> parse(String command);

    @Shadow protected abstract ParseResults<CommandSource> parse(String command);

    private static final Cache<Integer, InventoryOpenEvent> INVENTORY_OPEN_EVENTS = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();

    //TODO: make better
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessage(String content, CallbackInfo ci) {
        ci.cancel();
        var event = new ChatSendEvent(content);
        if (!EventBus.INSTANCE.fireResult(event)) return;

        content = TextKt.composeHex(event.getText());

        Instant instant = Instant.now();
        long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
        LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = lastSeenMessagesCollector.collect();
        MessageSignatureData messageSignatureData = messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
        sendPacket(new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void sendChatCommand(String command, CallbackInfo ci) {
        ci.cancel();
        var event = new CommandEvent(command);
        if (!EventBus.INSTANCE.fireResult(event)) return;

        SignedArgumentList<CommandSource> signedArgumentList = SignedArgumentList.of(this.parse(command));
        if (signedArgumentList.arguments().isEmpty()) {
            sendPacket(new CommandExecutionC2SPacket(command));
        } else {
            Instant instant = Instant.now();
            long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
            LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
            ArgumentSignatureDataMap argumentSignatureDataMap = ArgumentSignatureDataMap.sign(signedArgumentList, (value) -> {
                MessageBody messageBody = new MessageBody(value, instant, l, lastSeenMessages.lastSeen());
                return this.messagePacker.pack(messageBody);
            });
            sendPacket(new ChatCommandSignedC2SPacket(command, instant, l, argumentSignatureDataMap, lastSeenMessages.update()));
        }
    }

    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        if (packet.getEntityType() == EntityType.LIGHTNING_BOLT && RenderFeature.INSTANCE.getNoStrikes()) {
            ci.cancel();
        } else if (packet.getEntityType() == EntityType.FALLING_BLOCK && RenderFeature.INSTANCE.getNoFalling()) {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();

        val event = EventBus.INSTANCE.fire(
                new InventoryOpenEvent(packet.getSyncId(), packet.getScreenHandlerType(), packet.getName(), false)
        );
        INVENTORY_OPEN_EVENTS.put(packet.getSyncId(), event);
        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread(((Packet) packet), (PacketListener) this, MinecraftClient.getInstance());
                HandledScreens.open(event.getScreenHandlerType(), MinecraftClient.getInstance(), event.getSyncId(), event.getTitle());
            }
        } else Inventories.INSTANCE.close(packet.getSyncId());
    }

    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();
        val event = EventBus.INSTANCE.fire(
                new InventoryFillEvent(
                        packet.getSyncId(), packet.getContents(), INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId()),
                        MinecraftClient.getInstance().player.currentScreenHandler, false
                )
        );
        if (!event.getOpenEvent().isCancelled() || !event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread((Packet) packet, (PacketListener) this, MinecraftClient.getInstance());
                PlayerEntity playerEntity = MinecraftClient.getInstance().player;
                if (packet.getSyncId() == 0) {
                    playerEntity.playerScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
                } else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId) {
                    playerEntity.currentScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
                }
            }
        }
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;
        ci.cancel();

        val event = EventBus.INSTANCE.fire(
                new InventorySlotUpdateEvent(packet.getSyncId(), packet.getSlot(), packet.getStack(),
                        INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId()), false)
        );

        if (!event.isCancelled()) {
            if (!event.isHidden()) {
                NetworkThreadUtils.forceMainThread((Packet) packet, (PacketListener) this, MinecraftClient.getInstance());
                PlayerEntity playerEntity = MinecraftClient.getInstance().player;
                ItemStack itemStack = event.getStack();
                int i = event.getSlot();
                MinecraftClient.getInstance().getTutorialManager().onSlotUpdate(itemStack);
                if (event.getSyncId() == -1) {
                    if (!(MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen)) {
                        playerEntity.currentScreenHandler.setCursorStack(itemStack);
                    }
                } else if (event.getSyncId() == -2) {
                    playerEntity.getInventory().setStack(i, itemStack);
                } else {
                    boolean bl = false;
                    if (MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) {
                        CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen) MinecraftClient.getInstance().currentScreen;
                        bl = creativeInventoryScreen.isInventoryTabSelected();
                    }

                    if (event.getSyncId() == 0 && event.getSlot() >= 36 && i < 45) {
                        if (!itemStack.isEmpty()) {
                            ItemStack itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack();
                            if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
                                playerEntity.getItemCooldownManager()
                                        .set(itemStack.getItem(), 5);
                            }
                        }

                        playerEntity.playerScreenHandler.setStackInSlot(i, i, itemStack);
                    } else if (event.getSyncId() == playerEntity.currentScreenHandler.syncId && (event.getSyncId() != 0 || !bl)) {
                        playerEntity.currentScreenHandler.setStackInSlot(i, i, itemStack);
                    }
                }
            }
        }
    }

    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        if (packet.getSyncId() == 0) return;

        if (!EventBus.INSTANCE.fireResult(
                new InventoryCloseEvent(packet.getSyncId(), INVENTORY_OPEN_EVENTS.getIfPresent(packet.getSyncId())))) {
            ci.cancel();
        }
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    public void onCustomPayload(CustomPayload packet, CallbackInfo ci) {
        if (packet instanceof BrandCustomPayload){
            EventBus.INSTANCE.fire(ChangeServerEvent.INSTANCE);
        }

        //todo: checl
//        if (!EventBus.INSTANCE.fireResult(new CustomPayloadEvent(packet.getChannel().toString(), packet.getData()))) {
//            ci.cancel();
//        }
    }

    @Inject(method = "onChunkData", at = @At("TAIL"))
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        val chunk = MinecraftClient.getInstance().world.getChunk(packet.getChunkX(), packet.getChunkZ());
        EventBus.INSTANCE.fire(new ChunkLoadEvent(chunk));
    }

//    @Inject(method = "onResourcePackSend", at = @At("HEAD"), cancellable = true)
//    private void onResourcePackSend(ResourcePackSendS2CPacket packet, CallbackInfo ci) {
//        var event = new ResourcePackRequestEvent(packet, false);
//
//        if (!EventBus.INSTANCE.fireResult(event)) {
//            ci.cancel();
//            if (event.getResponseAccepted()) {
//                sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
//                sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED);
//            }
//        }
//    }

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new ParticleEvent(packet))) {
            ci.cancel();
        }
    }

    @Inject(method = "onMapUpdate", at = @At("HEAD"), cancellable = true)
    private void onParticle(MapUpdateS2CPacket packet, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new WorldMapEvent(packet.mapId().id(), packet))) {
            ci.cancel();
        }
    }

    @Redirect(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/TitleS2CPacket;text()Lnet/minecraft/text/Text;"))
    private Text onTitle(TitleS2CPacket instance) {
        return EventBus.INSTANCE.fire(new TitleEvent(instance.text(), false)).getTitle();
    }

    @Redirect(method = "onSubtitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SubtitleS2CPacket;text()Lnet/minecraft/text/Text;"))
    private Text onTitle(SubtitleS2CPacket instance) {
        return EventBus.INSTANCE.fire(new TitleEvent(instance.text(), true)).getTitle();
    }



//    @Inject(method = "onPlayerList", at = @At("TAIL"))
//    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
//        EventBus.INSTANCE.fire(new PlayerListEvent(packet.getActions(), packet.getEntries()));
//    }


}
