package ru.dargen.evoplus.features.fishing.widget

import ru.dargen.evoplus.util.minecraft.CurrentContainer

enum class FishingValueWidgetVisibleMode(val displayName: String, val isVisible: () -> Boolean) {

    ENABLED("Всегда", { true }),
    INVENTORY("Инвентарь", { CurrentContainer != null });

    override fun toString() = displayName

    companion object {
        fun ofIndex(index: Int): FishingValueWidgetVisibleMode {
            return entries.getOrNull(index) ?: ENABLED
        }
    }

}