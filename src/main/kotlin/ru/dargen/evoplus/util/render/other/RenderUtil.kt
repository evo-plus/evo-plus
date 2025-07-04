package ru.dargen.evoplus.util.render.other

import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.universal.ChatColor
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.vertex.UBufferBuilder
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.render.other.DrawHelper.writeRectCoords
import java.awt.Color
import kotlin.math.roundToInt
import kotlin.math.sqrt

object RenderUtil {

    internal fun <T> Color.withParts(block: (Int, Int, Int, Int) -> T) =
        block(this.red, this.green, this.blue, this.alpha)

    fun drawFilledBoundingBox(matrixStack: UMatrixStack, aabb: Box, c: Color, alphaMultiplier: Float = 1f, throughWalls: Boolean = false) {
        val buffer = UBufferBuilder.Companion.create(UGraphics.DrawMode.TRIANGLE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR)
        DrawHelper.writeFilledCube(buffer, matrixStack, aabb, c.multAlpha(alphaMultiplier))
        buffer.build()?.drawAndClose(if (throughWalls) SRenderPipelines.noDepthBoxPipeline else SRenderPipelines.boxPipeline)
    }

    @JvmStatic
    fun drawOutlinedBoundingBox(aabb: Box?, color: Color, width: Float, partialTicks: Float, throughWalls: Boolean = false) {
        if (aabb == null) return
        val buffer = UBufferBuilder.Companion.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR)
        val matrices = UMatrixStack.Compat.get()
        matrices.push()
        DrawHelper.setupCameraTransformations(matrices)
        RenderSystem.lineWidth(width)
        DrawHelper.writeOutlineCube(buffer, matrices, aabb, color.multAlpha(1f))
        buffer.build()?.drawAndClose(if (throughWalls) SRenderPipelines.noDepthBoxPipeline else SRenderPipelines.boxPipeline)
        matrices.pop()
    }

    @JvmStatic
    fun renderItem(itemStack: ItemStack?, x: Int, y: Int) {
        DrawHelper.drawItemOnGUI(UMatrixStack.Compat.get(), itemStack ?: ItemStack.EMPTY, x.toDouble(), y.toDouble())
        DrawHelper.drawStackOverlay(UMatrixStack.Compat.get(), itemStack ?: ItemStack.EMPTY, x.toDouble(), y.toDouble())
    }

    @JvmStatic
    fun renderTexture(
        texture: Identifier?,
        x: Int,
        y: Int,
        width: Int = 16,
        height: Int = 16
    ) {
        if (texture == null) return
        DrawHelper.drawTexture(UMatrixStack.Companion.UNIT, SRenderPipelines.guiTexturePipeline, texture, x.toDouble(), y.toDouble(), width = width.toDouble(), height = height.toDouble())
    }

    fun draw3DLine(
        pos1: Vec3d,
        pos2: Vec3d,
        width: Int,
        color: Color,
        partialTicks: Float,
        matrixStack: UMatrixStack,
        alphaMultiplier: Float = 1f
    ) {
        matrixStack.push()
        DrawHelper.setupCameraTransformations(matrixStack)
        RenderSystem.lineWidth(width.toFloat())
        val fixedColor = color.multAlpha(alphaMultiplier)
        val buffer = UBufferBuilder.Companion.create(UGraphics.DrawMode.LINE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR)
        buffer.pos(matrixStack, pos1.x, pos1.y, pos1.z).color(fixedColor).endVertex()
        buffer.pos(matrixStack, pos2.x, pos2.y, pos2.z).color(fixedColor).endVertex()
        buffer.build()?.drawAndClose(SRenderPipelines.linesPipeline)
        matrixStack.pop()
    }

    fun draw3DLineStrip(
        points: Iterable<Vec3d>,
        width: Int,
        color: Color,
        partialTicks: Float,
        matrixStack: UMatrixStack,
        alphaMultiplier: Float = 1f
    ) {
        matrixStack.push()
        DrawHelper.setupCameraTransformations(matrixStack)
        RenderSystem.lineWidth(width.toFloat())
        val fixedColor = color.multAlpha(alphaMultiplier)
        val buffer = UBufferBuilder.Companion.create(UGraphics.DrawMode.LINE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR)
        for (pos in points) {
            buffer.pos(matrixStack, pos.x, pos.y, pos.z).color(fixedColor).endVertex()
        }
        buffer.build()?.drawAndClose(SRenderPipelines.linesPipeline)
        matrixStack.pop()
    }

    fun drawLabel(
        pos: Vec3d,
        text: String,
        color: Color,
        partialTicks: Float,
        matrixStack: UMatrixStack,
        shadow: Boolean = false,
        scale: Float = 1f
    ) = drawNametag(pos.x, pos.y, pos.z, text, color, partialTicks, matrixStack, shadow, scale, false)

    fun renderWaypointText(str: String, loc: BlockPos, partialTicks: Float, matrixStack: UMatrixStack) =
        renderWaypointText(
            str,
            loc.x.toDouble(),
            loc.y.toDouble(),
            loc.z.toDouble(),
            partialTicks,
            matrixStack
        )

    fun renderWaypointText(
        str: String,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        matrixStack: UMatrixStack
    ) {
        matrixStack.push()
        val (viewerX, viewerY, viewerZ) = getViewerPos(partialTicks)
        val cameraEntity = Client.cameraEntity!!
        val distX = x - viewerX
        val distY = y - viewerY - cameraEntity.standingEyeHeight
        val distZ = z - viewerZ
        val dist = sqrt(distX * distX + distY * distY + distZ * distZ)
        val renderX: Double
        val renderY: Double
        val renderZ: Double
        if (dist > 12) {
            renderX = distX * 12 / dist + viewerX
            renderY = distY * 12 / dist + viewerY + cameraEntity.standingEyeHeight
            renderZ = distZ * 12 / dist + viewerZ
        } else {
            renderX = x
            renderY = y
            renderZ = z
        }
        drawNametag(renderX, renderY, renderZ, str, Color.WHITE, partialTicks, matrixStack)
        drawNametag(
            renderX,
            renderY - 0.25,
            renderZ,
            "${ChatColor.YELLOW}${dist.roundToInt()}m",
            Color.WHITE,
            partialTicks,
            matrixStack
        )
        matrixStack.pop()
    }

    private fun drawNametag(
        x: Double, y: Double, z: Double,
        str: String, color: Color,
        partialTicks: Float, matrixStack: UMatrixStack,
        shadow: Boolean = true, scale: Float = 1f, background: Boolean = true
    ) {
        matrixStack.push()
        DrawHelper.cameraOffset(matrixStack)
        DrawHelper.drawNametag(matrixStack, str, x, y, z, shadow, scale, background)
        matrixStack.pop()
    }

    fun getViewerPos(partialTicks: Float): Triple<Double, Double, Double> {
        val viewer = Client.cameraEntity!!
        val viewerX = viewer.lastRenderX + (viewer.x - viewer.lastRenderX) * partialTicks
        val viewerY = viewer.lastRenderY + (viewer.y - viewer.lastRenderY) * partialTicks
        val viewerZ = viewer.lastRenderZ + (viewer.z - viewer.lastRenderZ) * partialTicks
        return Triple(viewerX, viewerY, viewerZ)
    }

    fun Slot.highlight(color: Color) {
        val matrices = UMatrixStack()
        DrawHelper.setupContainerScreenTransformations(matrices)
        val buffer = UBufferBuilder.Companion.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR)
        writeRectCoords(matrices, buffer, x.toDouble(), y.toDouble(), x + 16.0, y + 16.0, color)
        buffer.build()?.drawAndClose(SRenderPipelines.guiPipeline)
    }

    fun Color.bindColor() = RenderSystem.setShaderColor(this.red / 255f, this.green / 255f, this.blue / 255f, this.alpha / 255f)
    fun Color.withAlpha(alpha: Int): Int = (alpha.coerceIn(0, 255) shl 24) or (this.rgb and 0x00ffffff)

    fun Color.multAlpha(mult: Float) = Color(
        red,
        green,
        blue,
        (alpha * mult).toInt().coerceIn(0, 255)
    )

}