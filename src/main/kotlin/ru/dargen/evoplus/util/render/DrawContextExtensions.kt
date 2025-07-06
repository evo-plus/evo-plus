package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.font.TextRenderer.TextLayerType
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import ru.dargen.evoplus.util.math.Vector3
import java.awt.Color

object DrawContextExtensions {

    fun DrawContext.drawText(
        text: String,
        x: Float = 0f, y: Float = 0f,
        shadow: Boolean = false,
        color: Color = Color.WHITE
    ) = if (shadow) drawTextWithShadow(text, x, y, color) else drawText(text, x, y, color)

//    fun DrawContext.drawText(
//        text: String,
//        x: Float = 0f, y: Float = 0f,
//        shadow: Boolean = false,
//        color: Color = Color.WHITE
//    ) {
//        val colorInt = color.rgb
//
//        if (GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)) {
//            val scissorBox = IntArray(4)
//            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, scissorBox)
//
//            val textWidth = TextRenderer.getWidth(text)
//            val textHeight = TextRenderer.fontHeight
//
//            val window = Client.window
//            val scaleFactor = window.scaleFactor
//
//            val gameScissorX = (scissorBox[0] / scaleFactor).toInt()
//            val gameScissorY = (window.scaledHeight - (scissorBox[1] + scissorBox[3]) / scaleFactor).toInt()
//            val gameScissorWidth = (scissorBox[2] / scaleFactor).toInt()
//            val gameScissorHeight = (scissorBox[3] / scaleFactor).toInt()
//
//            if (x >= gameScissorX + gameScissorWidth ||
//                x + textWidth <= gameScissorX ||
//                y >= gameScissorY + gameScissorHeight ||
//                y + textHeight <= gameScissorY) return
//
//            if (x < gameScissorX || x + textWidth > gameScissorX + gameScissorWidth ||
//                y < gameScissorY || y + textHeight > gameScissorY + gameScissorHeight) {
//                val scissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST)
//
//                val matrices = matrices
//                matrices.push()
//
//                GL11.glDisable(GL11.GL_DEPTH_TEST)
//
//                val clipX = gameScissorX.coerceAtLeast(x.toInt())
//                val clipY = gameScissorY.coerceAtLeast(y.toInt())
//                val clipWidth = (gameScissorX + gameScissorWidth).coerceAtMost((x + textWidth).toInt()) - clipX
//                val clipHeight = (gameScissorY + gameScissorHeight).coerceAtMost((y + textHeight).toInt()) - clipY
//
//                enableScissor(clipX, clipY, clipWidth, clipHeight)
//
//                drawText(TextRenderer, text, x.toInt(), y.toInt(), colorInt, shadow)
//
//                disableScissor()
//                if (scissorEnabled) {
//                    GL11.glEnable(GL11.GL_SCISSOR_TEST)
//                    GL11.glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3])
//                }
//                matrices.pop()
//                return
//            }
//
//        }
//        val matrices = matrices
//        matrices.push()
//        drawText(TextRenderer, text, x.toInt(), y.toInt(), colorInt, shadow)
//        matrices.pop()
//    }

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
            matrices.peek().positionMatrix,
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

    fun DrawContext.drawRectangle(size: Vector3, zLevel: Float = 0f, color: Color = Color.white) =
        drawRectangle(0f, 0f, size.x.toFloat(), size.y.toFloat(), zLevel, color)

    fun DrawContext.drawRectangle(
        minX: Float, minY: Float,
        maxX: Float, maxY: Float,
        zLevel: Float = 0f,
        color: Color = Color.white
    ) {
        val positionMatrix = matrices.peek().positionMatrix
        val tesselator = Tesselator

        val (r, g, b, a) = color.decomposeFloat()

        val buffer = tesselator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

        buffer.vertex(positionMatrix, minX, minY, zLevel).color(r, g, b, a)
        buffer.vertex(positionMatrix, minX, maxY, zLevel).color(r, g, b, a)
        buffer.vertex(positionMatrix, maxX, maxY, zLevel).color(r, g, b, a)
        buffer.vertex(positionMatrix, maxX, minY, zLevel).color(r, g, b, a)

        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(r, g, b, a)
        RenderSystem.setShader(ShaderProgramKeys.POSITION)

        BufferRenderer.drawWithGlobalProgram(buffer.end())

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()
    }

    fun DrawContext.drawCubeOutline(size: Vector3, color: Color = Color.white) =
        drawCubeOutline(0f, 0f, 0f, size.x.toFloat(), size.y.toFloat(), size.z.toFloat(), color)

    fun DrawContext.drawCubeOutline(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float,
        color: Color = Color.white
    ) {
        val position = matrices.peek().positionMatrix
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

}
