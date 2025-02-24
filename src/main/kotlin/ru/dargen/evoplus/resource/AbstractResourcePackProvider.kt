package ru.dargen.evoplus.resource

import net.minecraft.resource.*
import net.minecraft.resource.ResourcePackProfile.InsertionPosition
import net.minecraft.resource.ResourcePackProfile.Metadata
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import java.util.function.Consumer

abstract class AbstractResourcePackProvider(
    val id: String, val name: String, description: String,
) : ResourcePackProvider {

    private val metadata = ResourcePackInfo(
        id, Text.of(name), Source, Optional.empty()
    )
    protected open val pack
        get() = ResourcePackProfile.create(
            metadata, object : ResourcePackProfile.PackFactory {
                override fun open(info: ResourcePackInfo) = openPack(info)

                override fun openWithOverlays(info: ResourcePackInfo,metadata: Metadata) = openPack(info)

            },
            ResourceType.CLIENT_RESOURCES, ResourcePackPosition(true, InsertionPosition.TOP, true)
        )!!

    override fun register(profileAdder: Consumer<ResourcePackProfile>) = profileAdder.accept(pack)

    abstract fun openPack(info: ResourcePackInfo): ResourcePack

    companion object {
        val Source = ResourcePackSource.create(
            { Text.translatable("pack.nameAndSource", it, "evo-plus").formatted(Formatting.GRAY) }, true
        )
    }

}