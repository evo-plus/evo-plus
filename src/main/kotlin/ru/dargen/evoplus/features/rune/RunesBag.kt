package ru.dargen.evoplus.features.rune

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import ru.dargen.evoplus.event.input.KeyTypeEvent
import ru.dargen.evoplus.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.ScreenRenderEvent
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.boundKey
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.scheduler.schedule
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawText

private typealias RawRuneProperty = Pair<String, String>

object RunesBag {

    private const val MENU_NAME = "\uE962"

    private val RunePropertyPattern = "(.*): (.*)".toRegex()
    private val RunesBagSlots = listOf(11, 13, 15, 27, 35)
    private val RuneSetsSlots = listOf(0, 1, 3, 4, 5, 6, 8)
    private var RunesProperties = mapOf<String, RuneProperty>()
    private var SelectedSet = RuneSet(0, "", emptyList())

    init {
        on<KeyTypeEvent> {
            if (RuneFeature.RunesSetSwitch && CurrentScreen?.title?.string?.contains(MENU_NAME) == true) {
                var index = SelectedSet.id

                if (Keybinds.SelectRuneSetBinds.any { it.boundKey.code == key }) {
                    index = Keybinds.SelectRuneSetBinds.indexOfFirst { it.boundKey.code == key }
                } else index += (if (Keybinds.MoveRuneSetLeft.boundKey.code == key) -1
                else if (Keybinds.MoveRuneSetRight.boundKey.code == key) 1 else return@on)

                when {
                    index < 0 -> index = RuneSetsSlots.size - 1
                    index >= RuneSetsSlots.size -> index = 0
                }

                val clickedItem = CurrentScreenHandler?.getSlot(RuneSetsSlots[index])?.stack
                val firstLoreLine = clickedItem?.lore?.firstOrNull()?.string?.uncolored()

                if (firstLoreLine?.contains("Нажмите, чтобы использовать") == true)
                    Inventories.click(slot = RuneSetsSlots[index])
            }
        }

        on<InventoryFillEvent> {
            if (openEvent?.title?.string?.contains(MENU_NAME) == true) scheduleUpdate()
        }
        on<InventorySlotUpdateEvent> {
            if (openEvent?.title?.string?.contains(MENU_NAME) == true) scheduleUpdate()
        }

        on<ScreenRenderEvent.Post> {
            val scale = .9f
            val height = TextRenderer.fontHeight * scale

            if (screen !is GenericContainerScreen || MENU_NAME !in screen.title.string) return@on

            if (RuneFeature.RunesBagProperties) {
                val x = screen.width / 2.0 + 89.0
                val y = screen.height / 2.0 - (RunesProperties.size * height) / 2.0
                matrices.push()

                matrices.translate(x, y, .0)
                matrices.scale(scale, scale, scale)

                RunesProperties.values
                    .filter { it.value != .0 }
                    .forEachIndexed { index, property ->
                        matrices.drawText(property.toString(), 0f, index * height / scale, Colors.White)
                    }

                matrices.pop()
            }

            if (RuneFeature.RunesBagSet) {
                val x = screen.width / 2.0 - 89.0
                val y = screen.height / 2.0 - ((SelectedSet.runes.size + 1) * height) / 2.0
                matrices.push()

                matrices.translate(x, y, .0)
                matrices.scale(scale, scale, scale)

                matrices.drawText(
                    SelectedSet.name,
                    -TextRenderer.getWidth(SelectedSet.name).toFloat(), 0f,
                    Colors.White
                )
                SelectedSet.runes.forEachIndexed { index, line ->
                    matrices.drawText(
                        line,
                        -TextRenderer.getWidth(line).toFloat(), (index + 1) * height / scale,
                        Colors.White
                    )
                }

                matrices.pop()
            }
        }
    }

    private fun scheduleUpdate() = schedule(2) {
        if (RuneFeature.RunesBagSet) updateRuneSet()
        if (RuneFeature.RunesBagProperties) updateRunesProperties()
    }

    private fun updateRuneSet() {
        fetchSelectedRuneSet()?.let { SelectedSet = it }
    }

    private fun fetchSelectedRuneSet() =
        RuneSetsSlots.mapNotNull { CurrentScreenHandler?.getSlot(it)?.stack?.run { RuneSetsSlots.indexOf(it) to this } }
            .firstOrNull { it.second.lore.getOrNull(0)?.string?.uncolored() == "Используется" }
            ?.let { (id, item) -> RuneSet(id, item.name.string, item.lore.map(Text::getString).drop(2).dropLast(1)) }

    private fun updateRunesProperties() {
        RunesProperties = fetchRawRunesProperties()
            .groupByTo(mutableMapOf(), RawRuneProperty::first, RawRuneProperty::second)
            .mapValues { (name, values) ->
                var property: RuneProperty? = null
                values.forEach { value ->
                    val (type, matcher) = RuneProperty.Type.entries.firstNotNullOfOrNull {
                        it.pattern.find(value)?.run { it to this }
                    } ?: return@forEach
                    property = property ?: RuneProperty(name, type)
                    type.appender(property!!, matcher)
                }
                property
            }
            .filterValues { it?.value != null }
            .mapValues { it.value!! }
            .toList()
            .sortedByDescending { it.second.value }
            .toMap()

    }

    private fun fetchRawRunesProperties() =
        RunesBagSlots.mapNotNull { CurrentScreenHandler?.getSlot(it)?.stack?.takeIf { it.item == Items.PAPER } }
            .flatMap { it.fetchRawRunesProperties() }

    private fun ItemStack.fetchRawRunesProperties() =
        lore.map(Text::getString)
            .map(String::uncolored)
            .dropLastWhile { "-----" !in it }
            .dropWhile { "-----" !in it }
            .mapNotNull { RunePropertyPattern.find(it)?.run { groupValues[1] to groupValues[2] } }

}