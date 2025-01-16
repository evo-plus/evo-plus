package ru.dargen.evoplus.mixin.render.hud;

import com.mojang.authlib.GameProfile;
import lombok.val;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.player.AccessPlayerNameEvent;
import ru.dargen.evoplus.api.event.player.PlayerDisplayNameEvent;
import ru.dargen.evoplus.features.misc.MiscFeature;
import ru.dargen.evoplus.features.potion.PotionFeature;
import ru.dargen.evoplus.protocol.Connector;
import ru.dargen.evoplus.protocol.registry.PotionType;
import ru.dargen.evoplus.util.format.TimeKt;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;", ordinal = 1))
    private List<OrderedText> render(TextRenderer instance, StringVisitable text, int width) {
        val stringBuilder = new StringBuilder();
        val showServerInTab = MiscFeature.INSTANCE.getShowServerInTab();


        //TODO: remove from mixin to event
        if (PotionFeature.INSTANCE.getEnabledPotionsInTab()) {
            val potionTimers = PotionFeature.INSTANCE.getPotionTimers();
            if (!potionTimers.isEmpty()) {
                stringBuilder.append("\n§e§lАктивные эффекты §r§8(%s)".formatted(potionTimers.size()));
                potionTimers.forEach((potionId, potionState) -> {
                    var type = PotionType.Companion.byOrdinal(potionId);
                    if (type == null) return;

                    stringBuilder.append("\n%s (%s%%) §f%s".formatted(
                            type.getDisplayName(),
                            potionState.getQuality(),
                            TimeKt.getAsShortTextTime(potionState.getEndTime() - System.currentTimeMillis()))
                    );
                });
                stringBuilder.append("\n");
            }
        }

        if (showServerInTab) {
            stringBuilder.append(text.getString())
                    .append("\nТекущий сервер: §e%s".formatted(Connector.INSTANCE.getServer().getDisplayName()));
        }

        text = Text.of(stringBuilder.toString());
        return instance.wrapLines(text, width);
    }

    @Inject(at = @At("TAIL"), method = "getPlayerName", cancellable = true)
    private void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        var playerName = entry.getProfile().getName();

        var event = EventBus.INSTANCE.fire(new PlayerDisplayNameEvent(playerName, cir.getReturnValue()));
        cir.setReturnValue(event.getDisplayName());
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getName()Ljava/lang/String;"), method = "getPlayerName")
    private String getName(GameProfile profile) {
        var event = new AccessPlayerNameEvent(profile.getName(), profile.getName());
        return EventBus.INSTANCE.fire(event).getName();
    }

}
