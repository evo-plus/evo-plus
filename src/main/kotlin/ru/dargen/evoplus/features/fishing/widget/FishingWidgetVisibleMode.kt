package ru.dargen.evoplus.features.fishing.widget

import net.minecraft.item.Items
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.util.minecraft.Player

enum class FishingWidgetVisibleMode(val displayName: String, val isVisible: () -> Boolean) {

    ENABLED("Всегда", {true}),
    FISHING("На рыбалке", { PlayerDataCollector.location.isFish }),
    FISH_ROD("С удочкой", { Player?.mainHandStack?.item === Items.FISHING_ROD });

    override fun toString() = displayName

}