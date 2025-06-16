package ru.dargen.evoplus.mixin.render.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.dargen.evoplus.extension.ArmorStandEntityExtension;
import ru.dargen.evoplus.util.minecraft.MinecraftKt;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (isSelfAccessoryStand(entity) && MinecraftKt.getClient().options.getPerspective().isFirstPerson())
            cir.cancel();
    }

    @Unique
    private boolean isSelfAccessoryStand(T entity) {
        return entity.getType() == EntityType.ARMOR_STAND && ((ArmorStandEntityExtension) entity).evo_plus$isSelfAccessory();
    }

}
