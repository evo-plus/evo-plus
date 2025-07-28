package ru.dargen.evoplus.render.node.world

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.DrawUtil

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

    override fun renderBox(matrices: MatrixStack) {
//        val box = Box(position.x, position.y, position.z, size.x, size.y, size.z).print("box")
//        val box = Box(position.x, position.y, position.z, size.x, size.y, size.z).print("box")

        if (!isSeeThrough) RenderSystem.enableDepthTest()
        DrawUtil.draw3DBox(matrices, position, position.plus(size), color, width.toFloat())
        if (!isSeeThrough) RenderSystem.disableDepthTest()

    }

}

fun cubeOutline(block: CubeOutlineNode.() -> Unit = {}) = CubeOutlineNode().apply(block)