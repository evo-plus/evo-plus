package ru.dargen.evoplus.event.resourcepack

import net.minecraft.resource.ResourcePackProvider
import ru.dargen.evoplus.event.Event

data class ResourcePackProvidersEvent(val providers: MutableSet<ResourcePackProvider>) : Event