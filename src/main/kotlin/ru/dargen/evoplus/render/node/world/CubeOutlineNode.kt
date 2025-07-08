package ru.dargen.evoplus.render.node.world

import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.DrawUtil
import ru.dargen.evoplus.util.text.print
import java.awt.Color

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

    override fun renderElement(context: DrawContext, tickDelta: Float) {

        val offsetX = position.x - (origin.x * scaledSize.x)
        val offsetY = position.y - (origin.y * scaledSize.y)
        val offsetZ = position.z - (origin.z * scaledSize.z)

        val box = Box(
            offsetX, offsetY, offsetZ,
            offsetX + scaledSize.x, offsetY + scaledSize.y, offsetZ + scaledSize.z
        ).print("box1")

        context.drawCubeOutline(isWorldElement, box, color, 4 * width.toFloat())

    }

}

private fun DrawContext.drawCubeOutline(isWorldElement: Boolean, box: Box, color: Color, width: Float) {
    if (!isWorldElement) return

    val test = Box.from(Vec3d(-599.0, 97.0, 106.0))

    DrawUtil.draw3DBox(matrices, test, color, width)
}

fun cubeOutline(block: CubeOutlineNode.() -> Unit = {}) = CubeOutlineNode().apply(block)