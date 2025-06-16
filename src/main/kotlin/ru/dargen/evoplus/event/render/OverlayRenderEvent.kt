package ru.dargen.evoplus.event.render

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class OverlayRenderEvent(matrices: MatrixStack, tickDelta: Float) : RenderEvent(matrices, tickDelta) {

    companion object {
        lateinit var context: DrawContext
    }

}