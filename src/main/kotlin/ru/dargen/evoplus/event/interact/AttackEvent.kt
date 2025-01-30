package ru.dargen.evoplus.event.interact

import net.minecraft.entity.Entity
import ru.dargen.evoplus.event.CancellableEvent

class AttackEvent(val entity: Entity) : CancellableEvent()