package ru.dargen.evoplus.util.text

import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

fun MutableText.withFont(font: Identifier) = styled { it.withFont(font) }