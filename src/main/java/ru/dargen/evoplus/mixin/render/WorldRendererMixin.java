package ru.dargen.evoplus.mixin.render;

import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
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

    @Shadow
    private Frustum frustum;

//    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/Fog;)V", shift = At.Shift.BEFORE))
//    private void beforeRender(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
//
//        DrawContext context = new DrawContext(
//                MinecraftClient.getInstance(),
//                bufferBuilders.getEntityVertexConsumers()
//        );
//
//        context.getMatrices().push();
//        context.getMatrices().translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
//
//        EventBus.INSTANCE.fire(new WorldRenderEvent.Absolute(context, tickCounter.getTickDelta(false), camera, bufferBuilders));
//        context.getMatrices().pop();
//
//        EventBus.INSTANCE.fire(new WorldRenderEvent(context, tickCounter.getTickDelta(false), camera, bufferBuilders));
//    }

    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", cancellable = false)
    public void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.multiplyPositionMatrix(positionMatrix);

        Vec3d camPos = camera.getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

        EventBus.INSTANCE.fire(new WorldRenderEvent(matrixStack, frustum, tickCounter));

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

    }

}
