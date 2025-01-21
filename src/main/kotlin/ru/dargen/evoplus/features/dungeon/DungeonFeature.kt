package ru.dargen.evoplus.features.dungeon

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.customItem

object DungeonFeature : Feature("dungeon", "Данжи", customItem(Items.PAPER, 14)) {

    val DecorationHighlight by settings.boolean("Подсветка разрушаемых декораций", true)

    init {
        DungeonDecorationHighlight
    }

}