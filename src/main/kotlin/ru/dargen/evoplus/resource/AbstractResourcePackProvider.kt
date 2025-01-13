package ru.dargen.evoplus.resource

import net.minecraft.SharedConstants
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourcePackProfile.InsertionPosition
import net.minecraft.resource.ResourcePackProfile.Metadata
import net.minecraft.resource.ResourcePackProvider
import net.minecraft.resource.ResourcePackSource
import net.minecraft.resource.ResourceType
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.function.Consumer

abstract class AbstractResourcePackProvider(
    val id: String, val name: String, val description: String,
) : ResourcePackProvider {

    private val metadata = Metadata(
        Text.of(description),
        SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES),
        FeatureFlags.DEFAULT_ENABLED_FEATURES
    )
    protected open val pack get() = ResourcePackProfile.of(
        id, Text.of(name), true,
        this::openPack, metadata,
        ResourceType.CLIENT_RESOURCES, InsertionPosition.TOP, false, Source
    )

    override fun register(profileAdder: Consumer<ResourcePackProfile>) = profileAdder.accept(pack)

    abstract fun openPack(name: String): ResourcePack

    companion object {
        val Source = ResourcePackSource.create(
            { Text.translatable("pack.nameAndSource", it, "evo-plus").formatted(Formatting.GRAY) }, true
        )
    }

}