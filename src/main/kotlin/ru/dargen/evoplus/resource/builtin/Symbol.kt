package ru.dargen.evoplus.resource.builtin

import net.minecraft.text.Text
import ru.dargen.evoplus.resource.identifier
import ru.dargen.evoplus.util.text.withFont

object Symbol {

    val IconsFont = identifier("icons")

    @JvmField
    val EP = Text.of("\uE000").copy().withFont(IconsFont)

}