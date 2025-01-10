package ru.dargen.evoplus.resource.builtin

import net.minecraft.resource.DirectoryResourcePack
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ZipResourcePack
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.resource.AbstractResourcePackProvider

class EvoPlusPackProvider : AbstractResourcePackProvider("evo-plus", "EvoPlus", "Mod additional pack") {

    override val pack = super.pack

    override fun openPack(name: String): ResourcePack {
        return if (EvoPlus.DevEnvironment) DirectoryResourcePack(name, EvoPlus.Path, true)
        else ZipResourcePack(name, EvoPlus.Path.toFile(), true)
    }

}