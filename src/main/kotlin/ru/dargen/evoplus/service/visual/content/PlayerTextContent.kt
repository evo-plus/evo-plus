package ru.dargen.evoplus.service.visual.content

import net.minecraft.text.*
import ru.dargen.evoplus.extension.MutableTextExtension
import ru.dargen.evoplus.util.kotlin.invoke
import ru.dargen.evoplus.util.kotlin.safeCast
import java.util.*

data class PlayerTextContent(val options: PlayerContentOptions, val player: String, val text: String) : TextContent {

    fun buildString() = buildString {
        if (options.prefix) append("§f\uE558§r ")
        append(text)
    }

    override fun <T : Any> visit(visitor: StringVisitable.StyledVisitor<T>, style: Style): Optional<T> {
        return visitor.accept(style, buildString())
    }

    override fun <T : Any> visit(visitor: StringVisitable.Visitor<T>): Optional<T> {
        return visitor.accept(buildString())
    }

    override fun toString(): String {
        return "player{$player, $text}"
    }

    companion object {

        fun prefix(player: String, text: String) =
            MutableText.of(PlayerTextContent(PlayerContentOptions.Prefix, player, text))

        fun replace(text: Text, player: String, options: PlayerContentOptions): Text {
            text.content?.safeCast<LiteralTextContent>()?.let { content ->
                if (content.string.contains(player) == true) {
                    text<MutableTextExtension>()?.setTextContent(PlayerTextContent(options, player, content.string))
                }
            }
            text.siblings.filterIsInstance<MutableText>().forEach { replace(it, player, options) }
            return text
        }

    }

}