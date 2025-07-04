package ru.dargen.evoplus.features.misc

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.event.inventory.InventoryClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider
import ru.dargen.evoplus.util.minecraft.CurrentScreenHandler
import ru.dargen.evoplus.util.minecraft.lore
import ru.dargen.evoplus.util.render.alpha
import ru.dargen.evoplus.util.render.other.RenderUtil.highlight
import ru.dargen.evoplus.util.selector.enumSelector
import java.awt.Color

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    val FullBright by settings.boolean("Полная яркость", true)
    val HighlightAvailableItems by settings.boolean("Подсветка доступных предметов", true)

    val HealthRender by settings.switcher("Режим отображения здоровья", enumSelector<HealthRenderMode>())

//    val HealthBarsRender by settings.boolean("Отображать полоску здоровья игроков", true) on { HealthBar::updateRender }
//    val HealthBarsY by settings.selector("Сдвиг полоски здоровья игроков", (0..50).toSelector()) { "${it?.div(10.0)?.fix(1)}" }
//    val HealthCountRender by settings.boolean("Отображать единицы здоровья игроков", true)

    val NoBlockParticles by settings.boolean("Отключение эффектов блока")
    val NoFire by settings.boolean("Отключение огня")
    val NoStrikes by settings.boolean("Отключение молний")
    val NoDamageShake by settings.boolean("Отключение покачивания камеры при ударе")
    val NoHandShake by settings.boolean("Отключение покачивания руки")
    val NoExcessHud by settings.boolean("Отключение ненужных элементов HUD", true)

    // фогост сказал убрать, т.к. она теперь интерактивная
//    val NoExpHud by settings.boolean("Отключение отрисовки опыта и его уровня", true)

    init {
//        HealthBar

        on<ResourcePackProvidersEvent> {
            providers.add(EvoPlusPackProvider())
        }

//        on<InventoryClickEvent> {
//            val itemSlot = CurrentScreenHandler?.getSlot(slot)
//            val itemStack = itemSlot?.stack ?: return@on
//
//            if (HighlightAvailableItems && !itemStack.isEmpty && isHighlightedItem(itemStack)) itemSlot.highlight(Colors.Green.alpha(100))
//        }

    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

    val HIGHLIGHT_DESCRIPTION = mutableListOf(
        "Нажмите, чтобы получить награду",
        "Нажмите, чтобы забрать награду"
    )

    fun isHighlightedItem(stack: ItemStack): Boolean {
        val lore = stack.lore

        if (lore.isEmpty()) return false

        return HIGHLIGHT_DESCRIPTION.contains(lore.last().toString())
    }

}