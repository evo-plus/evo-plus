package ru.dargen.evoplus.util.render

import gg.essential.universal.UMatrixStack
import gg.essential.universal.vertex.UBufferBuilder
import net.minecraft.util.math.Box
import java.awt.Color

object DrawHelper {


    fun drawRectangleCoords(
        matrices: UMatrixStack,
        buffer: UBufferBuilder,
        minX: Double, minY: Double,
        maxX: Double, maxY: Double,
        color: Color
    ) {
        buffer.pos(matrices, minX, minY, 0.0).color(color).endVertex()
        buffer.pos(matrices, minX, maxY, 0.0).color(color).endVertex()
        buffer.pos(matrices, maxX, maxY, 0.0).color(color).endVertex()
        buffer.pos(matrices, maxX, minY, 0.0).color(color).endVertex()
    }

    fun drawOutlineCube(
        buffer: UBufferBuilder,
        matrices: UMatrixStack,
        box: Box,
        color: Color
    ) {
        box.apply {
            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()
        }
    }

    fun drawFilledCube(
        buffer: UBufferBuilder,
        matrices: UMatrixStack,
        box: Box,
        color: Color
    ) {
        box.apply {
            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, minY, maxZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()

            buffer.pos(matrices, minX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, minX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, minZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()

            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()
            buffer.pos(matrices, maxX, maxY, maxZ).color(color).endVertex()
        }
    }

}