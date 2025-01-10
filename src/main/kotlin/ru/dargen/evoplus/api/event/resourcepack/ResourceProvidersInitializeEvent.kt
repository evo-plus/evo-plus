package ru.dargen.evoplus.api.event.resourcepack

import net.minecraft.resource.ResourcePackProvider
import ru.dargen.evoplus.api.event.Event

class ResourceProvidersInitializeEvent(val providers: MutableSet<ResourcePackProvider>) : Event {
}