package ru.dargen.evoplus.resource.builtin

import net.minecraft.resource.DirectoryResourcePack
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackInfo
import net.minecraft.resource.ZipResourcePack
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.resource.AbstractResourcePackProvider

class EvoPlusPackProvider : AbstractResourcePackProvider("evo-plus", "EvoPlus", "Mod additional pack") {

    override val pack = super.pack

    override fun openPack(info: ResourcePackInfo): ResourcePack {
        return if (EvoPlus.DevEnvironment) DirectoryResourcePack.DirectoryBackedFactory(EvoPlus.Path).open(info)
        else ZipResourcePack.ZipBackedFactory(EvoPlus.Path.toFile()).open(info)
    }

}