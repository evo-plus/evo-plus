package ru.dargen.evoplus.event.inventory

import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class InventoryEvent(val syncId: Int) : CancellableEvent()