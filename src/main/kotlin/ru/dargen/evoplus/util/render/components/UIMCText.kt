package ru.dargen.evoplus.util.render.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.unstable.state.v2.State
import gg.essential.elementa.unstable.state.v2.stateOf
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Text
import java.awt.Color

class UIMCText(
    val text: State<Text>,
    val shadow: State<Color?> = stateOf(null) // null if no shadow
) : UIComponent() {

    override fun draw(matrixStack: UMatrixStack) {
        val text = text.getUntracked()
        if (text.string.isEmpty())
            return

        beforeDrawCompat(matrixStack)

        UGraphics.enableBlend()
        drawText(matrixStack)
        super.draw(matrixStack)
    }

    private fun drawText(matrixStack: UMatrixStack) {
        val text = text.getUntracked()
        val vertexConsumer = UMinecraft.getMinecraft().bufferBuilders.entityVertexConsumers

        shadow.getUntracked()?.let { shadowColor ->
            UMinecraft.getMinecraft().textRenderer.draw(text, 1f, 1f, shadowColor.rgb, false, matrixStack.peek().model, vertexConsumer, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
        }
        UMinecraft.getMinecraft().textRenderer.draw(text, 0f, 0f, getColor().rgb, false, matrixStack.peek().model, vertexConsumer, TextRenderer.TextLayerType.NORMAL, 0, 15728880)
        vertexConsumer.draw()
    }

}
