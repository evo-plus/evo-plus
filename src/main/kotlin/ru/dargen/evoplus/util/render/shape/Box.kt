package ru.dargen.evoplus.util.render.shape

import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.vertex.UBufferBuilder
import net.minecraft.util.math.Box
import org.joml.Vector3f
import ru.dargen.evoplus.util.render.DrawHelper
import java.awt.Color

data class Box(
    var box: Box,
    var color: Color
) {

    fun draw() {

        val matrixStack = UMatrixStack.Compat.get()

        matrixStack.runWithGlobalState {
            val buffer = UBufferBuilder.create(UGraphics.DrawMode.LINES, UGraphics.CommonVertexFormats.POSITION_COLOR)

            DrawHelper.drawOutlineCube(buffer, matrixStack, box, color)
        }

//        matrices.push {
//            RenderSystem.enableBlend()
//            RenderSystem.defaultBlendFunc()
//            RenderSystem.disableCull()
//            RenderSystem.disableDepthTest()
//            if (centered) translate(position.x - size.x / 2, position.y - size.y / 2, position.z - size.z / 2)
//            else translate(position.x, position.y, position.z)
//
//            matrices.drawBoxOutline(0f, 0f, 0f, size.x, size.y, size.z, color)
//            RenderSystem.enableDepthTest()
//        }
    }

}