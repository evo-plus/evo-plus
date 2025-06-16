package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.render.OverlayRenderEvent
import ru.dargen.evoplus.util.math.Vector3
import java.awt.Color

fun MatrixStack.drawText(
    text: String,
    x: Float = 0f, y: Float = 0f,
    shadow: Boolean = false,
    color: Color = Color.WHITE
) = if (shadow) drawTextWithShadow(text, x, y, color) else drawText(text, x, y, color)

fun MatrixStack.drawText(
    text: String,
    position: Vector3 = Vector3.Zero,
    shadow: Boolean = false,
    color: Color = Color.WHITE
) = drawText(text, position.x.toFloat(), position.y.toFloat(), shadow, color)

fun MatrixStack.drawText(text: String, position: Vector3 = Vector3.Zero, color: Color = Color.WHITE) =
    drawText(text, position.x.toFloat(), position.y.toFloat(), color)

fun MatrixStack.drawText(text: String, x: Float = 0f, y: Float = 0f, color: Color = Color.WHITE) =
    TextRenderer.draw(text, x, y, color.rgb, false, positionMatrix, OverlayRenderEvent.context.vertexConsumers, TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE)

fun MatrixStack.drawTextWithShadow(text: String, position: Vector3 = Vector3.Zero, color: Color = Color.WHITE) =
    drawTextWithShadow(text, position.x.toFloat(), position.y.toFloat(), color)

fun MatrixStack.drawTextWithShadow(text: String, x: Float = 0f, y: Float = 0f, color: Color = Color.WHITE) =
    TextRenderer.draw(text, x, y, color.rgb, true, positionMatrix, OverlayRenderEvent.context.vertexConsumers, TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE)

fun MatrixStack.drawWorldText(
    text: String, x: Float, y: Float,
    shadow: Boolean = false, isSeeThrough: Boolean = false,
    color: Color = Color.WHITE
) = TextRenderer.draw(
    text, x, y, color.rgb, shadow,
    positionMatrix, OverlayRenderEvent.context.vertexConsumers,
    if (isSeeThrough) TextLayerType.SEE_THROUGH else TextLayerType.NORMAL,
    0, LightmapTextureManager.MAX_LIGHT_COORDINATE
)

fun MatrixStack.drawWorldText(
    text: String, position: Vector3 = Vector3.Zero,
    shadow: Boolean = false, isSeeThrough: Boolean = false,
    color: Color = Color.WHITE
) = drawWorldText(text, position.x.toFloat(), position.y.toFloat(), shadow, isSeeThrough, color)


fun MatrixStack.drawRectangle(size: Vector3, zLevel: Float = 0f, color: Color = Color.white) =
    drawRectangle(0f, 0f, size.x.toFloat(), size.y.toFloat(), zLevel, color)

fun MatrixStack.drawRectangle(
    minX: Float, minY: Float,
    maxX: Float, maxY: Float,
    zLevel: Float = 0f,
    color: Color = Color.white
) {
    val positionMatrix = positionMatrix
    val buffer = Tesselator

    val (r, g, b, a) = color.decomposeFloat()

    val begin = buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION)

    begin.vertex(positionMatrix, minX, minY, zLevel).color(r, g, b, a)
    begin.vertex(positionMatrix, minX, maxY, zLevel).color(r, g, b, a)
    begin.vertex(positionMatrix, maxX, maxY, zLevel).color(r, g, b, a)
    begin.vertex(positionMatrix, maxX, minY, zLevel).color(r, g, b, a)

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR)

    BufferRenderer.drawWithGlobalProgram(begin.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}

fun MatrixStack.drawCubeOutline(size: Vector3, color: Color = Color.white) =
    drawCubeOutline(0f, 0f, 0f, size.x.toFloat(), size.y.toFloat(), size.z.toFloat(), color)

fun MatrixStack.drawCubeOutline(
    minX: Float, minY: Float, minZ: Float,
    maxX: Float, maxY: Float, maxZ: Float,
    color: Color = Color.white
) {
    val position = positionMatrix
    val buffer = Tesselator

    val (r, g, b, a) = color.decomposeFloat()

    val begin = buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION)

    // Bottom edges
    begin.vertex(position, minX, minY, minZ).color(r, g, b, a)
    begin.vertex(position, maxX, minY, minZ).color(r, g, b, a)

    begin.vertex(position, maxX, minY, minZ).color(r, g, b, a)
    begin.vertex(position, maxX, minY, maxZ).color(r, g, b, a)

    begin.vertex(position, maxX, minY, maxZ).color(r, g, b, a)
    begin.vertex(position, minX, minY, maxZ).color(r, g, b, a)

    begin.vertex(position, minX, minY, maxZ).color(r, g, b, a)
    begin.vertex(position, minX, minY, minZ).color(r, g, b, a)

    // Top edges
    begin.vertex(position, minX, maxY, minZ).color(r, g, b, a)
    begin.vertex(position, maxX, maxY, minZ).color(r, g, b, a)

    begin.vertex(position, maxX, maxY, minZ).color(r, g, b, a)
    begin.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)

    begin.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)
    begin.vertex(position, minX, maxY, maxZ).color(r, g, b, a)

    begin.vertex(position, minX, maxY, maxZ).color(r, g, b, a)
    begin.vertex(position, minX, maxY, minZ).color(r, g, b, a)

    // Vertical edges
    begin.vertex(position, minX, minY, minZ).color(r, g, b, a)
    begin.vertex(position, minX, maxY, minZ).color(r, g, b, a)

    begin.vertex(position, maxX, minY, minZ).color(r, g, b, a)
    begin.vertex(position, maxX, maxY, minZ).color(r, g, b, a)

    begin.vertex(position, maxX, minY, maxZ).color(r, g, b, a)
    begin.vertex(position, maxX, maxY, maxZ).color(r, g, b, a)

    begin.vertex(position, minX, minY, maxZ).color(r, g, b, a)
    begin.vertex(position, minX, maxY, maxZ).color(r, g, b, a)

    RenderSystem.enableBlend()
    RenderSystem.setShaderColor(r, g, b, a)
    RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR)

    BufferRenderer.drawWithGlobalProgram(begin.end())

    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.disableBlend()
}