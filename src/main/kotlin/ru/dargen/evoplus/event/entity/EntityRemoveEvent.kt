package ru.dargen.evoplus.event.entity

import net.minecraft.entity.Entity
import ru.dargen.evoplus.event.Event

data class EntityRemoveEvent(val entity: Entity) : Event