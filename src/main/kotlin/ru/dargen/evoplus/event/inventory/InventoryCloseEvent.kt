package ru.dargen.evoplus.event.inventory

import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.event.Event

class InventoryCloseEvent(val syncId: Int, val openEvent: InventoryOpenEvent?) : CancellableEvent()