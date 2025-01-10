package ru.dargen.evoplus.extension;

import net.minecraft.resource.ResourcePackProvider;

import java.util.Set;

public interface ResourcePackManagerExtension {

    void appendProviders(Set<ResourcePackProvider> providers);

}