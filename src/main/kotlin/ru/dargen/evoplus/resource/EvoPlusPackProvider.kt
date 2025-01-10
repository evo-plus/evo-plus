package ru.dargen.evoplus.resource

import net.minecraft.SharedConstants
import net.minecraft.resource.DirectoryResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourcePackProfile.InsertionPosition
import net.minecraft.resource.ResourcePackProvider
import net.minecraft.resource.ResourcePackSource
import net.minecraft.resource.ResourceType
import net.minecraft.resource.ZipResourcePack
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.dargen.evoplus.EvoPlus
import java.util.function.Consumer

class EvoPlusPackProvider : ResourcePackProvider {

    val packSource = ResourcePackSource.create({
        Text.translatable("pack.nameAndSource", it, "evo-plus").formatted(Formatting.GRAY)
    }, true)

    val packMetadata = ResourcePackProfile.Metadata(
        Text.of("Mod additional pack"),
        SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES),
        FeatureFlags.DEFAULT_ENABLED_FEATURES
    )
    val packFactory = fun(name: String) =
        if (EvoPlus.DevEnvironment) DirectoryResourcePack(name, EvoPlus.Path, true)
        else ZipResourcePack(name, EvoPlus.Path.toFile(), true)

    val resourcePack = ResourcePackProfile.of(
        "evo-plus", Text.of("EvoPlus"), true, packFactory, packMetadata,
        ResourceType.CLIENT_RESOURCES, InsertionPosition.TOP, true, packSource
    )

    override fun register(profileAdder: Consumer<ResourcePackProfile>) {
        profileAdder.accept(resourcePack)
    }

}