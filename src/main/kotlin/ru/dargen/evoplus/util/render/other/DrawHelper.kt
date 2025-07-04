package ru.dargen.evoplus.util.render.other

import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.render.URenderPipeline
import gg.essential.universal.vertex.UBufferBuilder
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.OverlayTexture
import net.minecraft.item.ItemStack
import net.minecraft.util.Colors
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.client.texture.TextureManager
import net.minecraft.item.ModelTransformationMode
import org.joml.Quaternionf
import ru.dargen.evoplus.mixin.render.screen.HandledScreenAccessor
import ru.dargen.evoplus.util.minecraft.Client
import java.awt.Color

object DrawHelper {

    private val textureManager = Client.textureManager

    fun cameraOffset(matrices: UMatrixStack) {
        val cameraPos = Client.gameRenderer.camera.pos.negate()

        matrices.translate(cameraPos.x, cameraPos.y, cameraPos.z)
    }

    fun cameraRotation(matrices: UMatrixStack) {
        matrices.multiply(Client.gameRenderer.camera.rotation.conjugate(Quaternionf()))
    }

    fun setupCameraTransformations(matrices: UMatrixStack) {
        cameraRotation(matrices)
        cameraOffset(matrices)
    }

    fun setupContainerScreenTransformations(matrices: UMatrixStack, aboveItems: Boolean = false) {
        val screen = Client.currentScreen as? HandledScreenAccessor ?: error("Current screen does not implement AccessorGuiContainer")
        matrices.translate(screen.guiLeft.toFloat(), screen.guiTop.toFloat(), 0f)
        if (aboveItems) {
            matrices.translate(0f, 0f, 100f + 150f + 1f)
        }
    }

    fun writeOutlineCube(
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

    fun writeFilledCube(
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

    fun drawTexture(
        matrices: UMatrixStack,
        pipeline: URenderPipeline,
        sprite: Identifier,
        x: Double,
        y: Double,
        u: Double = 0.0,
        v: Double = 0.0,
        width: Double,
        height: Double,
        textureWidth: Double = width,
        textureHeight: Double = height,
        color: Color = Color.WHITE
    ) {
        val buffer = UBufferBuilder.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR)
        UGraphics.bindTexture(0, sprite)
        val texture = textureManager.getTexture(sprite)
        texture.setFilter(false, false)
        val glTexture = texture.glId
        val x2 = x + width
        val y2 = y + height
        val u1 = u / textureWidth
        val u2 = (u + width) / textureWidth
        val v1 = v / textureHeight
        val v2 = (v + height) / textureHeight
        buffer.pos(matrices, x, y, 0.0).tex(u1, v1).color(color).endVertex()
        buffer.pos(matrices, x, y2, 0.0).tex(u1, v2).color(color).endVertex()
        buffer.pos(matrices, x2, y2, 0.0).tex(u2, v2).color(color).endVertex()
        buffer.pos(matrices, x2, y, 0.0).tex(u2, v1).color(color).endVertex()
        buffer.build()?.drawAndClose(pipeline) {
            texture(0, glTexture)
        }
    }

    fun drawNametag(matrices: UMatrixStack, text: String, x: Double, y: Double, z: Double, shadow: Boolean = true, scale: Float = 1f, background: Boolean = true, throughWalls: Boolean = false) {
        matrices.push()
        matrices.translate(x, y + 0.5, z)
        matrices.multiply(Client.entityRenderDispatcher.rotation)
        matrices.scale(0.025f, -0.025f, 0.025f)

        matrices.scale(scale, scale, scale)
        val centerPos = UGraphics.getStringWidth(text) / -2f
        val backgroundColor = if (!background) 0 else (Client.options.getTextBackgroundOpacity(0.25f) * 255).toInt() shl 24
        Client.textRenderer.draw(
            text,
            centerPos,
            0f,
            Colors.WHITE,
            shadow,
            matrices.peek().model,
            Client.bufferBuilders.entityVertexConsumers,
            if (throughWalls) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL,
            backgroundColor,
            15728880
        )
        matrices.pop()
    }

    fun writeRect(matrices: UMatrixStack, buffer: UBufferBuilder, x: Double, y: Double, width: Double, height: Double, color: Color) {
        writeRectCoords(matrices, buffer, x, y, x + width, y + height, color)
    }

    fun writeRectCoords(
        matrices: UMatrixStack,
        buffer: UBufferBuilder,
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double,
        color: Color
    ) {
        buffer.pos(matrices, x1, y1, 0.0).color(color).endVertex()
        buffer.pos(matrices, x1, y2, 0.0).color(color).endVertex()
        buffer.pos(matrices, x2, y2, 0.0).color(color).endVertex()
        buffer.pos(matrices, x2, y1, 0.0).color(color).endVertex()
    }

    fun drawItemOnGUI(matrices: UMatrixStack, stack: ItemStack, x: Double, y: Double, z: Double = 100.0, dynamicDisplay: Boolean = true) {
        if (stack.isEmpty) return
        matrices.push()
        matrices.translate(x + 8, y + 8, (150 + z))

        matrices.scale(16.0f, -16.0f, 16.0f)

        Client.itemRenderer.renderItem(if (dynamicDisplay) Client.player else null, stack, ModelTransformationMode.GUI, false, matrices.toMC(), Client.bufferBuilders.entityVertexConsumers, Client.world, 15728880, OverlayTexture.DEFAULT_UV, 0)

        matrices.pop()
    }

    fun drawStackOverlay(matrices: UMatrixStack, stack: ItemStack, x: Double, y: Double, stackCountText: String? = null) {
        if (stack.isEmpty) return
        matrices.push()
        drawItemBar(matrices, stack, x, y)
        drawStackCount(matrices, stack, x, y, stackCountText)
        drawCooldownProgress(matrices, stack, x, y)
        matrices.pop()
    }

    fun drawItemBar(matrices: UMatrixStack, stack: ItemStack, x: Double, y: Double) {
        if (stack.isItemBarVisible) {
            val i = x + 2
            val j = y + 13
            val buffer = UBufferBuilder.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR)
            writeRectCoords(matrices, buffer, i, j, i + 13, j + 2, Color.BLACK)
            matrices.translate(0f, 0f, 200f)
            writeRectCoords(
                matrices,
                buffer,
                i,
                j,
                i + stack.itemBarStep,
                j + 1,
                Color(stack.itemBarColor)
            )
            matrices.translate(0f, 0f, -200f)
            buffer.build()?.drawAndClose(SRenderPipelines.guiPipeline)
        }
    }

    fun drawStackCount(matrices: UMatrixStack, stack: ItemStack, x: Double, y: Double, stackCountText: String?) {
        if (stack.count != 1 || stackCountText != null) {
            val string = stackCountText ?: stack.count.toString()
            matrices.push()
            matrices.translate(0.0f, 0.0f, 200.0f)
            UGraphics.drawString(
                matrices,
                string,
                (x + 19 - 2 - UGraphics.getStringWidth(string)).toFloat(),
                (y + 6 + 3).toFloat(),
                Colors.WHITE,
                true
            )
            matrices.pop()
        }
    }

    fun drawCooldownProgress(matrices: UMatrixStack, stack: ItemStack?, x: Double, y: Double) {
        val clientPlayerEntity = Client.player
        val f = clientPlayerEntity?.itemCooldownManager
            ?.getCooldownProgress(stack, Client.renderTickCounter.getTickDelta(true)) ?: 0f
        if (f > 0.0f) {
            val i = y + MathHelper.floor(16.0f * (1.0f - f))
            val j = i + MathHelper.ceil(16.0f * f)
            val buffer = UBufferBuilder.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR)
            matrices.translate(0f, 0f, 200f)
            writeRectCoords(matrices, buffer, x, i, x + 16, j, Color(Int.MAX_VALUE, true))
            matrices.translate(0f, 0f, -200f)
            buffer.build()?.drawAndClose(SRenderPipelines.guiPipeline)
        }
    }

}
