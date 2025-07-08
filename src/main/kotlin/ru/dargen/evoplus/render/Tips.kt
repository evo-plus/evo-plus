package ru.dargen.evoplus.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.minecraft.MousePosition
import ru.dargen.evoplus.util.render.DrawUtil.drawText
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.translate
import ru.dargen.evoplus.util.text.print
import java.awt.Color

object Tips {

    fun draw(
        context: DrawContext, vararg lines: String, position: Vector3 = MousePosition.apply { x += 5 },
        space: Float = 1.0f, indent: Float = 2.5f,
        color: Color = Colors.TransparentBlack,
        textColor: Color = Colors.White, shadow: Boolean = false
    ) {
        val width = lines.maxOf(TextRenderer::getWidth).toFloat() + indent * 2f
        val height = lines.size * TextRenderer.fontHeight + (lines.size - 1) * space + indent * 2

        if (width + position.x > Overlay.WindowSize.x)
            position.x = (Overlay.WindowSize.x - width) - 1

        if (height + position.y > Overlay.WindowSize.y)
            position.y = (Overlay.WindowSize.y - height) - 1


        context.matrices.push()
        context.matrices.loadIdentity()
        RenderSystem.disableScissor()

        context.matrices.translate(position)
        context.matrices.translate(0f, 0f, 1000f) //z buffer hehe
        context.fill(0, 0, width.toInt(), height.toInt(), color.print("color2").rgb)

        context.matrices.translate(indent, indent, 0f)

        lines.forEach {
            context.drawText(it, shadow = shadow, color = textColor)

            context.matrices.translate(0f, TextRenderer.fontHeight + space, 0f)
        }

        context.matrices.pop()
    }


}