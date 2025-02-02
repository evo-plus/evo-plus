package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.math.Vector3
import java.awt.Color

fun MatrixStack.drawText(
    text: String,
    x: Float = 0f, y: Float = 0f,
    shadow: Boolean = false,
    color: Color = Colors.White
) = if (shadow) drawTextWithShadow(text, x, y, color) else drawText(text, x, y, color)

fun MatrixStack.drawText(
    text: String,
    position: Vector3 = Vector3.Zero,
    shadow: Boolean = false,
    color: Color = Colors.White
) = drawText(text, position.x.toFloat(), position.y.toFloat(), shadow, color)

fun MatrixStack.drawText(text: String, position: Vector3 = Vector3.Zero, color: Color = Colors.White) =
    drawText(text, position.x.toFloat(), position.y.toFloat(), color)

fun MatrixStack.drawText(text: String, x: Float = 0f, y: Float = 0f, color: Color = Colors.White) =
    TextRenderer.draw(this, text, x, y, color.rgb)

fun MatrixStack.drawTextWithShadow(text: String, position: Vector3 = Vector3.Zero, color: Color = Colors.White) =
    drawTextWithShadow(text, position.x.toFloat(), position.y.toFloat(), color)

fun MatrixStack.drawTextWithShadow(text: String, x: Float = 0f, y: Float = 0f, color: Color = Colors.White) =
    TextRenderer.drawWithShadow(this, text, x, y, color.rgb)

fun MatrixStack.drawWorldText(
    text: String, x: Float, y: Float,
    shadow: Boolean = false, isSeeThrough: Boolean = false,
    color: Color = Colors.White
) = TextRenderer.draw(
    text, x, y, color.rgb, shadow,
    positionMatrix, BufferBuilderStorage.entityVertexConsumers,
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
    val positionMatrix = positionMatrix
    val buffer = Tesselator.buffer

    val (r, g, b, a) = color.decomposeFloat()

    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)

    buffer.vertex(positionMatrix, minX, minY, zLevel).next()
    buffer.vertex(positionMatrix, minX, maxY, zLevel).next()
    buffer.vertex(positionMatrix, maxX, maxY, zLevel).next()
    buffer.vertex(positionMatrix, maxX, minY, zLevel).next()

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}

fun MatrixStack.drawBoxOutline(min: Vector3f, max: Vector3f, color: Color = Colors.White) =
    drawBoxOutline(min.x, min.y, min.z, max.x, max.y, max.z, color)

fun MatrixStack.drawBoxOutline(
    minX: Float, minY: Float, minZ: Float,
    maxX: Float, maxY: Float, maxZ: Float,
    color: Color = Colors.White
) {
    val position = positionMatrix
    val buffer = Tesselator.buffer

    val (r, g, b, a) = color.decomposeFloat()

    buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION)

    // Bottom edges
    buffer.vertex(position, minX, minY, minZ).next()
    buffer.vertex(position, maxX, minY, minZ).next()

    buffer.vertex(position, maxX, minY, minZ).next()
    buffer.vertex(position, maxX, minY, maxZ).next()

    buffer.vertex(position, maxX, minY, maxZ).next()
    buffer.vertex(position, minX, minY, maxZ).next()

    buffer.vertex(position, minX, minY, maxZ).next()
    buffer.vertex(position, minX, minY, minZ).next()

    // Top edges
    buffer.vertex(position, minX, maxY, minZ).next()
    buffer.vertex(position, maxX, maxY, minZ).next()

    buffer.vertex(position, maxX, maxY, minZ).next()
    buffer.vertex(position, maxX, maxY, maxZ).next()

    buffer.vertex(position, maxX, maxY, maxZ).next()
    buffer.vertex(position, minX, maxY, maxZ).next()

    buffer.vertex(position, minX, maxY, maxZ).next()
    buffer.vertex(position, minX, maxY, minZ).next()

    // Vertical edges
    buffer.vertex(position, minX, minY, minZ).next()
    buffer.vertex(position, minX, maxY, minZ).next()

    buffer.vertex(position, maxX, minY, minZ).next()
    buffer.vertex(position, maxX, maxY, minZ).next()

    buffer.vertex(position, maxX, minY, maxZ).next()
    buffer.vertex(position, maxX, maxY, maxZ).next()

    buffer.vertex(position, minX, minY, maxZ).next()
    buffer.vertex(position, minX, maxY, maxZ).next()

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(GameRenderer::getPositionProgram)

    BufferRenderer.drawWithGlobalProgram(buffer.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}