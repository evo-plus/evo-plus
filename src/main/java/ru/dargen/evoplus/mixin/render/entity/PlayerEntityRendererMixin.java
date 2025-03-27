package ru.dargen.evoplus.mixin.render.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.render.entity.RenderPlayerLabelEvent;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V", at = @At("TAIL"))
    private void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo ci) {
        EventBus.INSTANCE.fire(new RenderPlayerLabelEvent(((EntityRendererAccessor) this).getDispatcher(), matrixStack, abstractClientPlayerEntity));
    }

}
