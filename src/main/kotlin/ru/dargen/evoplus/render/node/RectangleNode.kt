package ru.dargen.evoplus.render.node

import net.minecraft.client.gui.DrawContext
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class RectangleNode : Node() {

    var transparentRender = false

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        if (!transparentRender && color.alpha == 0) return

//        if (!isSeeThrough) RenderSystem.enableDepthTest()
        context.fill(0, 0, size.x.toInt(), size.y.toInt(), size.z.toInt(), color.rgb)
//        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun rectangle(block: RectangleNode.() -> Unit = {}) = RectangleNode().apply(block)