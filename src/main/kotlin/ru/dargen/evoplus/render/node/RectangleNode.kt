package ru.dargen.evoplus.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.render.DrawContextExtensions.drawRectangle

@KotlinOpens
class RectangleNode : Node() {

    var transparentRender = false

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        if (!transparentRender && color.alpha == 0) return

        if (!isSeeThrough) RenderSystem.enableDepthTest()
        context.drawRectangle(size, color = color)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun rectangle(block: RectangleNode.() -> Unit = {}) = RectangleNode().apply(block)