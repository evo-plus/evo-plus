package ru.dargen.evoplus.render.node.world

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import org.lwjgl.opengl.GL11
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.DrawContextExtensions.drawCubeOutline

@KotlinOpens
class CubeOutlineNode : Node() {

    var scaledSize
        get() = size / 40.0
        set(value) {
            this.size = value * 40.0
        }
    var width by proxied(1.0)

    init {
        scaledSize = v3(1.0, 1.0, 1.0)
    }

//    override fun renderElement(context: DrawContext, tickDelta: Float) {
//        if (!isSeeThrough) RenderSystem.enableDepthTest()
//        //TODO: make line width
//        RenderSystem.lineWidth(4 * width.toFloat())
//        context.drawCubeOutline(size, color)
//        if (!isSeeThrough) RenderSystem.disableDepthTest()
//    }

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        val lineWidth = 4 * width.toFloat()

        val wasDepthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        val previousLineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH)

        try {
            if (!isSeeThrough) RenderSystem.enableDepthTest()
            else RenderSystem.disableDepthTest()

            RenderSystem.lineWidth(lineWidth)

            context.drawCubeOutline(size, color)

        } finally {
            if (wasDepthTestEnabled) RenderSystem.enableDepthTest()
            else RenderSystem.disableDepthTest()

            RenderSystem.lineWidth(previousLineWidth)
        }
    }

}

fun cubeOutline(block: CubeOutlineNode.() -> Unit = {}) = CubeOutlineNode().apply(block)