package ru.dargen.evoplus.mixin.network;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.game.BlockBreakEvent;
import ru.dargen.evoplus.api.event.inventory.InventoryClickEvent;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!EventBus.INSTANCE.fireResult(new InventoryClickEvent(syncId, slotId, button, actionType))) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "breakBlock")
    public void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        EventBus.INSTANCE.fire(new BlockBreakEvent(pos));
    }

}
