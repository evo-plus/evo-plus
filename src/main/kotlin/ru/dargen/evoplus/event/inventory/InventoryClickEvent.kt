package ru.dargen.evoplus.event.inventory

import net.minecraft.screen.slot.SlotActionType

class InventoryClickEvent(
    syncId: Int, val slot: Int,
    val button: Int, val action: SlotActionType
) : InventoryEvent(syncId)