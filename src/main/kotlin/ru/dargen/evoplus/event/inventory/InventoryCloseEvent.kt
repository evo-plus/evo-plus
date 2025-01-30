package ru.dargen.evoplus.event.inventory

import ru.dargen.evoplus.event.CancellableEvent

class InventoryCloseEvent(val syncId: Int, val openEvent: InventoryOpenEvent?) : CancellableEvent()