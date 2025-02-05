package ru.dargen.evoplus.render.node.world

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.drawCubeOutline

@KotlinOpens
class CubeOutlineNode : Node() {

    var scaledSize get() = size / 40.0
        set(value) {
            this.size = value * 40.0
        }
    var width by proxied(1.0)

    init {
        scaledSize = v3(1.0, 1.0, 1.0)
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        if (!isSeeThrough) RenderSystem.enableDepthTest()
        //TODO: make line width
        RenderSystem.lineWidth(4 * width.toFloat())
        matrices.drawCubeOutline(size, color)
        if (!isSeeThrough) RenderSystem.disableDepthTest()
    }

}

fun cubeOutline(block: CubeOutlineNode.() -> Unit = {}) = CubeOutlineNode().apply(block)