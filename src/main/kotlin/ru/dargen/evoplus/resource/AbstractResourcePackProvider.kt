package ru.dargen.evoplus.resource

import net.minecraft.resource.*
import net.minecraft.resource.ResourcePackProfile.InsertionPosition
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*
import java.util.function.Consumer

abstract class AbstractResourcePackProvider(
    val id: String, val name: String, description: String,
) : ResourcePackProvider {

    private val info = ResourcePackInfo(
        id, Text.of(name), Source, Optional.empty()
    )
    private val metadata = ResourcePackProfile.Metadata(
        Text.of(description), ResourcePackCompatibility.COMPATIBLE,
        FeatureSet.empty(), emptyList()
    )
    protected open val pack
        get() = ResourcePackProfile(
            info, object : ResourcePackProfile.PackFactory {
                override fun open(info: ResourcePackInfo) = openPack(info)
                override fun openWithOverlays(info: ResourcePackInfo, metadata: ResourcePackProfile.Metadata) = openPack(info)
            }, metadata, ResourcePackPosition(true, InsertionPosition.TOP, true)
        )


    override fun register(profileAdder: Consumer<ResourcePackProfile>) = profileAdder.accept(pack)

    abstract fun openPack(info: ResourcePackInfo): ResourcePack

    companion object {
        val Source = ResourcePackSource.create(
            { Text.translatable("pack.nameAndSource", it, "evo-plus").formatted(Formatting.GRAY) }, true
        )
    }

}