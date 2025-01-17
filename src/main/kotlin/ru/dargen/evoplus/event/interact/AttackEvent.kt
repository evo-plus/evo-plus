package ru.dargen.evoplus.event.interact

import net.minecraft.entity.Entity
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.event.Event

class AttackEvent(val entity: Entity) : CancellableEvent()