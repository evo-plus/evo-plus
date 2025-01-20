package ru.dargen.evoplus.features.fishing.widget

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.render.node.asyncTick
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.kotlin.invoke
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.items
import ru.dargen.evoplus.util.minecraft.lore
import kotlin.math.max

class FishingValueWidget(val name: String, val pattern: Regex) : WidgetBase {

    override val node = text("$name: 0") {
        isShadowed = true

        asyncTick {
            val value = max(
                CurrentScreen<GenericContainerScreen>()?.screenHandler?.stacks?.collect() ?: 0,
                Player!!.inventory.items.collect()
            )

            text = "$name: $value"
        }
    }

    private fun Collection<ItemStack>.collect() = mapNotNull { item ->
        item.lore.getOrNull(2)
            ?.string
            ?.let { pattern.find(it.trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.times(item.count) }
    }.sum().takeIf { it > 0 } ?: 0

}