package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.minecraft.Client
import java.awt.Color

fun MatrixStack.drawText(
    text: String,
    x: Float = 0f, y: Float = 0f,
    shadow: Boolean = false,
    color: Color = Colors.White
) = Client.textRenderer.draw(
    text, x, y, color.rgb,
    shadow, peek().positionMatrix,
    BufferBuilderStorage.entityVertexConsumers,
    TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE
)

fun MatrixStack.drawText(
    text: String,
    position: Vector3 = Vector3.Zero,
    color: Color = Colors.White,
    shadow: Boolean = false
) = drawText(text, position.x.toFloat(), position.y.toFloat(), shadow, color)

fun MatrixStack.drawText(text: String, position: Vector3 = Vector3.Zero, color: Color = Colors.White) =
    drawText(text, position.x.toFloat(), position.y.toFloat(), false, color)

fun MatrixStack.drawTextWithShadow(text: String, position: Vector3 = Vector3.Zero, color: Color = Colors.White) =
    drawText(text, position.x.toFloat(), position.y.toFloat(), true, color)

fun MatrixStack.drawTextWithShadow(text: String, x: Float = 0f, y: Float = 0f, color: Color = Colors.White) =
    drawText(text, x, y, true, color)

fun MatrixStack.drawWorldText(
    text: String, x: Float, y: Float,
    shadow: Boolean = false, isSeeThrough: Boolean = false,
    color: Color = Colors.White
) = Client.textRenderer.draw(
    text, x, y, color.rgb, shadow,
    peek().positionMatrix,
    BufferBuilderStorage.entityVertexConsumers,
    if (isSeeThrough) TextLayerType.SEE_THROUGH else TextLayerType.NORMAL,
    0, LightmapTextureManager.MAX_LIGHT_COORDINATE
)

fun MatrixStack.drawWorldText(
    text: String, position: Vector3 = Vector3.Zero,
    shadow: Boolean = false, isSeeThrough: Boolean = false,
    color: Color = Colors.White
) = drawWorldText(text, position.x.toFloat(), position.y.toFloat(), shadow, isSeeThrough, color)


fun MatrixStack.drawRectangle(size: Vector3, zLevel: Float = 0f, color: Color = Colors.White) =
    drawRectangle(0f, 0f, size.x.toFloat(), size.y.toFloat(), zLevel, color)

fun MatrixStack.drawRectangle(
    minX: Float, minY: Float,
    maxX: Float, maxY: Float,
    zLevel: Float = 0f,
    color: Color = Colors.White
) {
    val matrix = peek().positionMatrix
    val tessellator = Tessellator.getInstance()
    val buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

    val (r, g, b, a) = color.decomposeFloat()

    buffer.vertex(matrix, minX, minY, zLevel).color(r, g, b, a)
    buffer.vertex(matrix, minX, maxY, zLevel).color(r, g, b, a)
    buffer.vertex(matrix, maxX, maxY, zLevel).color(r, g, b, a)
    buffer.vertex(matrix, maxX, minY, zLevel).color(r, g, b, a)

    RenderSystem.enableBlend()
    RenderSystem.setShader(GameRenderer::getPositionColorProgram)
    BufferRenderer.drawWithGlobalProgram(buffer.end())
    RenderSystem.disableBlend()
}

fun MatrixStack.drawBoxOutline(min: Vector3f, max: Vector3f, color: Color = Colors.White) =
    drawBoxOutline(min.x, min.y, min.z, max.x, max.y, max.z, color)

fun MatrixStack.drawBoxOutline(
    minX: Float, minY: Float, minZ: Float,
    maxX: Float, maxY: Float, maxZ: Float,
    color: Color = Colors.White
) {
    val matrix = peek().positionMatrix
    val tessellator = Tessellator.getInstance()
    val buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION)

    val (r, g, b, a) = color.decomposeFloat()

    // Bottom edges
    buffer.vertex(matrix, minX, minY, minZ)
    buffer.vertex(matrix, maxX, minY, minZ)

    buffer.vertex(matrix, maxX, minY, minZ)
    buffer.vertex(matrix, maxX, minY, maxZ)

    buffer.vertex(matrix, maxX, minY, maxZ)
    buffer.vertex(matrix, minX, minY, maxZ)

    buffer.vertex(matrix, minX, minY, maxZ)
    buffer.vertex(matrix, minX, minY, minZ)

    // Top edges
    buffer.vertex(matrix, minX, maxY, minZ)
    buffer.vertex(matrix, maxX, maxY, minZ)

    buffer.vertex(matrix, maxX, maxY, minZ)
    buffer.vertex(matrix, maxX, maxY, maxZ)

    buffer.vertex(matrix, maxX, maxY, maxZ)
    buffer.vertex(matrix, minX, maxY, maxZ)

    buffer.vertex(matrix, minX, maxY, maxZ)
    buffer.vertex(matrix, minX, maxY, minZ)

    // Vertical edmatrix
    buffer.vertex(matrix, minX, minY, minZ)
    buffer.vertex(matrix, minX, maxY, minZ)

    buffer.vertex(matrix, maxX, minY, minZ)
    buffer.vertex(matrix, maxX, maxY, minZ)

    buffer.vertex(matrix, maxX, minY, maxZ)
    buffer.vertex(matrix, maxX, maxY, maxZ)

    buffer.vertex(matrix, minX, minY, maxZ)
    buffer.vertex(matrix, minX, maxY, maxZ)

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}