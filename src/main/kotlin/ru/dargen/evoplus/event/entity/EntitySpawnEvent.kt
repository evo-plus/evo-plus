package ru.dargen.evoplus.event.entity

import net.minecraft.entity.Entity
import ru.dargen.evoplus.event.CancellableEvent

class EntitySpawnEvent(val entity: Entity) : CancellableEvent()