package ru.dargen.evoplus.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.player.AccessPlayerNameEvent;
import ru.dargen.evoplus.event.player.PlayerDisplayNameEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract Text getName();

    @Shadow public abstract String getNameForScoreboard();

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if (getName() != null) {
            var event = EventBus.INSTANCE.fire(new PlayerDisplayNameEvent(getName().toString(), cir.getReturnValue()));
            cir.setReturnValue(event.getDisplayName());
        }
    }

    @Redirect(method = "getName", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/GameProfile;getName()Ljava/lang/String;"))
    private String getName(GameProfile profile) {
        var event = new AccessPlayerNameEvent(profile.getName(), profile.getName());
        return EventBus.INSTANCE.fire(event).getName();
    }

}
