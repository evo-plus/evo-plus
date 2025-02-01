package ru.dargen.evoplus.resource.diamondworld

import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ZipResourcePack
import ru.dargen.evoplus.features.misc.resource.ResourcePackDownloader
import ru.dargen.evoplus.resource.AbstractResourcePackProvider
import java.util.function.Consumer

class DiamondWorldPackProvider(
    private val downloader: ResourcePackDownloader,
    private val isEnabled: () -> Boolean,
) : AbstractResourcePackProvider("diamond-world", "DiamondWorld", "Server resource pack") {

    override fun register(profileAdder: Consumer<ResourcePackProfile>) {
        if (isEnabled()) {
            super.register(profileAdder)
        }
    }

    override fun openPack(name: String): ResourcePack {
        return ZipResourcePack(name, downloader.supplySync().toFile(), true)
    }

}