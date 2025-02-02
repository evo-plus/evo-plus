package ru.dargen.evoplus.event.render

import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RenderEvent(val matrices: MatrixStack, val tickDelta: Float) : CancellableEvent()

@KotlinOpens
class WorldRenderEvent(
    matrices: MatrixStack,
    tickDelta: Float,
    val camera: Camera,
    val bufferBuilderStorage: BufferBuilderStorage,
) : RenderEvent(matrices, tickDelta) {

    class Absolute(
        matrices: MatrixStack,
        tickDelta: Float,
        camera: Camera,
        bufferBuilderStorage: BufferBuilderStorage,
    ) : WorldRenderEvent(matrices, tickDelta, camera, bufferBuilderStorage)

}
