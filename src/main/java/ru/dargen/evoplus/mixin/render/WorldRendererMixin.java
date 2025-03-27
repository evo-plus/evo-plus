package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.event.render.WorldRenderEvent;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/Camera;)V", shift = At.Shift.BEFORE))
    private void beforeRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        //Absolute from 0,0,0
        MatrixStack matrices = new MatrixStack();

        matrices.push();
        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);

        EventBus.INSTANCE.fire(new WorldRenderEvent.Absolute(matrices, tickCounter.getTickDelta(false), camera, bufferBuilders));
        matrices.pop();

        EventBus.INSTANCE.fire(new WorldRenderEvent(matrices, tickCounter.getTickDelta(false), camera, bufferBuilders));
    }

}
