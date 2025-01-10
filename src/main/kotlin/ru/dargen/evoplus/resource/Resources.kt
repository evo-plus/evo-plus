package ru.dargen.evoplus.resource

import net.minecraft.util.Identifier
import ru.dargen.evoplus.EvoPlus

fun identifier(name: String) = Identifier(EvoPlus.Id, name)

fun texture(name: String) = identifier("textures/$name.png")