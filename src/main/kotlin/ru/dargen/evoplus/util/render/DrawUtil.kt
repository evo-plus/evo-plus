package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import ru.dargen.evoplus.util.math.Vector3
import java.awt.Color


object DrawUtil {

    fun DrawContext.drawText(
        text: String,
        x: Float = 0f, y: Float = 0f,
        shadow: Boolean = false,
        color: Color = Color.WHITE
    ) = if (shadow) drawTextWithShadow(text, x, y, color) else drawText(text, x, y, color)

    fun DrawContext.drawText(
        text: String,
        position: Vector3 = Vector3.Zero,
        shadow: Boolean = false,
        color: Color = Color.WHITE
    ) = drawText(text, position.x.toFloat(), position.y.toFloat(), shadow, color)

    fun DrawContext.drawText(text: String, position: Vector3 = Vector3.Zero, color: Color = Color.WHITE) =
        drawText(text, position.x.toFloat(), position.y.toFloat(), color)

    fun DrawContext.drawText(text: String, x: Float = 0f, y: Float = 0f, color: Color = Color.WHITE) =
        TextRenderer.draw(
            text,
            x,
            y,
            color.rgb,
            false,
            matrices.peek().positionMatrix,
            vertexConsumers,
            TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

    fun DrawContext.drawTextWithShadow(text: String, position: Vector3 = Vector3.Zero, color: Color = Color.WHITE) =
        drawTextWithShadow(text, position.x.toFloat(), position.y.toFloat(), color)

    fun DrawContext.drawTextWithShadow(text: String, x: Float = 0f, y: Float = 0f, color: Color = Color.WHITE) =
        TextRenderer.draw(
            text,
            x,
            y,
            color.rgb,
            true,
            matrices.positionMatrix,
            vertexConsumers,
            TextLayerType.NORMAL,
            0,
            LightmapTextureManager.MAX_LIGHT_COORDINATE
        )

    fun DrawContext.drawWorldText(
        text: String, x: Float, y: Float,
        shadow: Boolean = false, isSeeThrough: Boolean = false,
        color: Color = Color.WHITE
    ) = TextRenderer.draw(
        text, x, y, color.rgb, shadow,
        matrices.peek().positionMatrix, vertexConsumers,
        if (isSeeThrough) TextLayerType.SEE_THROUGH else TextLayerType.NORMAL,
        0, LightmapTextureManager.MAX_LIGHT_COORDINATE
    )

    fun DrawContext.drawWorldText(
        text: String, position: Vector3 = Vector3.Zero,
        shadow: Boolean = false, isSeeThrough: Boolean = false,
        color: Color = Color.WHITE
    ) = drawWorldText(text, position.x.toFloat(), position.y.toFloat(), shadow, isSeeThrough, color)

    fun MatrixStack.drawCubeOutline(size: Vector3, color: Color = Color.white) =
        drawCubeOutline(0f, 0f, 0f, size.x.toFloat() + 1, size.y.toFloat() + 1, size.z.toFloat() + 1, color)

    @Suppress("Duplicates")
    fun MatrixStack.drawCubeOutline(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        color: Color = Color.white
    ) {
        val position = positionMatrix
        val tesselator = Tesselator

        val (r, g, b, a) = color.decomposeFloat()

        val buffer = tesselator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR)

        // Bottom edges
        buffer.vertex(position, minX, minY, minZ).color(r, g, b, a)
        buffer.vertex(position, maxX, minY, minZ).color(r, g, b, a)

        buffer.vertex(position, maxX, minY, minZ).color(r, g, b, a)
        buffer.vertex(position, maxX, minY, maxZ).color(r, g, b, a)

        buffer.vertex(position, maxX, minY, maxZ).color(r, g, b, a)
        buffer.vertex(position, minX, minY, maxZ).color(r, g, b, a)

        buffer.vertex(position, minX, minY, maxZ).color(r, g, b, a)
        buffer.vertex(position, minX, minY, minZ).color(r, g, b, a)

        // Top edges
        buffer.vertex(position, minX, maxY, minZ).color(r, g, b, a)
        buffer.vertex(position, maxX, maxY, minZ).color(r, g, b, a)

        buffer.vertex(position, maxX, maxY, minZ).color(r, g, b, a)
        buffer.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)

        buffer.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)
        buffer.vertex(position, minX, maxY, maxZ).color(r, g, b, a)

        buffer.vertex(position, minX, maxY, maxZ).color(r, g, b, a)
        buffer.vertex(position, minX, maxY, minZ).color(r, g, b, a)

        // Vertical edges
        buffer.vertex(position, minX, minY, minZ).color(r, g, b, a)
        buffer.vertex(position, minX, maxY, minZ).color(r, g, b, a)

        buffer.vertex(position, maxX, minY, minZ).color(r, g, b, a)
        buffer.vertex(position, maxX, maxY, minZ).color(r, g, b, a)

        buffer.vertex(position, maxX, minY, maxZ).color(r, g, b, a)
        buffer.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)

        buffer.vertex(position, minX, minY, maxZ).color(r, g, b, a)
        buffer.vertex(position, minX, maxY, maxZ).color(r, g, b, a)

        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(r, g, b, a)
        RenderSystem.setShader(ShaderProgramKeys.POSITION)

        BufferRenderer.drawWithGlobalProgram(buffer.end())

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()
    }

    fun draw3DFilledBox(matrixStack: MatrixStack, position: Vector3, size: Vector3, color: Color, lineThickness: Float) {
        draw3DFilledBox(matrixStack, position.x.toFloat(), position.y.toFloat(), position.z.toFloat(), size.x.toFloat(), size.y.toFloat(), size.z.toFloat(), color, lineThickness)
    }

    @Suppress("Duplicates")
    fun draw3DFilledBox(
        matrixStack: MatrixStack,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        color: Color, lineThickness: Float
    ) {

        RenderSystem.setShaderColor(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())

        val entry = matrixStack.peek()
        val matrix4f = entry.getPositionMatrix()

        val tessellator = RenderSystem.renderThreadTesselator()

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()

        RenderSystem.setShader(ShaderProgramKeys.POSITION)

        RenderSystem.setShaderColor(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())

        var bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)

        bufferBuilder.vertex(matrix4f, minX, minY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ)
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ)

        bufferBuilder.vertex(matrix4f, minX, maxY, minZ)
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ)

        bufferBuilder.vertex(matrix4f, minX, minY, minZ)
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ)

        bufferBuilder.vertex(matrix4f, maxX, minY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ)
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ)

        bufferBuilder.vertex(matrix4f, minX, minY, maxZ)
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ)
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ)
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ)

        bufferBuilder.vertex(matrix4f, minX, minY, minZ)
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ)
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ)
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ)

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES)

        RenderSystem.lineWidth(lineThickness)

        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)

        draw3DLine(matrixStack, bufferBuilder, minX, minY, minZ, maxX, minY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, minZ, maxX, minY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, maxZ, minX, minY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, maxZ, minX, minY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, minZ, minX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, minZ, maxX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, maxZ, maxX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, maxZ, minX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, maxY, minZ, maxX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, maxY, minZ, maxX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, maxY, maxZ, minX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, maxY, maxZ, minX, maxY, minZ, color)

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.enableCull()
        RenderSystem.lineWidth(1f)
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    fun draw3DBox(matrixStack: MatrixStack, position: Vector3, size: Vector3, color: Color, lineThickness: Float) {
        draw3DBox(matrixStack, position.x.toFloat(), position.y.toFloat(), position.z.toFloat(), size.x.toFloat(), size.y.toFloat(), size.z.toFloat(), color, lineThickness)
    }

    @Suppress("Duplicates")
    fun draw3DBox(
        matrixStack: MatrixStack,
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        color: Color, lineThickness: Float
    ) {
        RenderSystem.setShaderColor(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())

        val entry = matrixStack.peek()
        val matrix4f = entry.getPositionMatrix()

        val tessellator = RenderSystem.renderThreadTesselator()

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableCull()
        RenderSystem.disableDepthTest()

        RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES)

        RenderSystem.lineWidth(lineThickness)

        val bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)

        draw3DLine(matrixStack, bufferBuilder, minX, minY, minZ, maxX, minY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, minZ, maxX, minY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, maxZ, minX, minY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, maxZ, minX, minY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, minZ, minX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, minZ, maxX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, minY, maxZ, maxX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, minY, maxZ, minX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, maxY, minZ, maxX, maxY, minZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, maxY, minZ, maxX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, maxX, maxY, maxZ, minX, maxY, maxZ, color)
        draw3DLine(matrixStack, bufferBuilder, minX, maxY, maxZ, minX, maxY, minZ, color)

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())

        RenderSystem.enableCull()
        RenderSystem.lineWidth(1f)
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    @Suppress("Duplicates")
    private fun draw3DLine(
        matrixStack: MatrixStack, bufferBuilder: BufferBuilder, x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float, color: Color
    ) {
        val entry = matrixStack.peek()
        val matrix4f = entry.getPositionMatrix()

        val normalized = Vec3d((x2 - x1).toDouble(), (y2 - y1).toDouble(), (z2 - z1).toDouble()).normalize()

        val r = color.red.toFloat()
        val g = color.green.toFloat()
        val b = color.blue.toFloat()

        bufferBuilder
            .vertex(matrix4f, x1, y1, z1)
            .color(r, g, b, 1.0f)
            .normal(entry, normalized.x.toFloat(), normalized.y.toFloat(), normalized.z.toFloat())
        bufferBuilder
            .vertex(matrix4f, x2, y2, z2)
            .color(r, g, b, 1.0f)
            .normal(entry, normalized.x.toFloat(), normalized.y.toFloat(), normalized.z.toFloat())

    }

}
