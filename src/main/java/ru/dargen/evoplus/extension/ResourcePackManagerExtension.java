package ru.dargen.evoplus.extension;

import net.minecraft.resource.ResourcePackProvider;

import java.util.Set;

public interface ResourcePackManagerExtension {

    Set<ResourcePackProvider> getProviders();

    void addProvider(ResourcePackProvider provider);

    void removeProvider(ResourcePackProvider provider);

}
