package ru.dargen.evoplus.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.player.PlayerDisplayNameEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract String getEntityName();

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if (getEntityName() != null) {
            var event = EventBus.INSTANCE.fire(new PlayerDisplayNameEvent(getEntityName(), cir.getReturnValue()));
            cir.setReturnValue(event.getDisplayName());
        }
    }

}
