package ru.dargen.evoplus.event.render

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RenderEvent(val context: DrawContext, val tickDelta: Float) : CancellableEvent()

@KotlinOpens
class WorldRenderEvent(
    context: DrawContext,
    tickDelta: Float,
    val camera: Camera,
    val bufferBuilderStorage: BufferBuilderStorage,
) : RenderEvent(context, tickDelta) {

    class Absolute(
        context: DrawContext,
        tickDelta: Float,
        camera: Camera,
        bufferBuilderStorage: BufferBuilderStorage,
    ) : WorldRenderEvent(context, tickDelta, camera, bufferBuilderStorage)

}
