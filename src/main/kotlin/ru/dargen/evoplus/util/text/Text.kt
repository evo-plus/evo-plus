package ru.dargen.evoplus.util.text

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

val TextSpace = Text.of(" ")

fun MutableText.withFont(font: Identifier) = styled { it.withFont(font) }

fun <T> T.print(addition: String? = null): T {
    return apply { if (addition !== null) println("$addition: $this") else println(this) }
}