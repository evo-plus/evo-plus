package ru.dargen.evoplus.features.dungeon

import net.minecraft.item.Items
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.WorldMapEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.util.minecraft.customItem

object DungeonFeature : Feature("dungeon", "Данжи", customItem(Items.PAPER, 14)) {

    val DecorationHighlight by settings.boolean(
        "Подсветка разрушаемых декораций",
        true
    )
//    val Map by widgets.widget("Карта", widget = DungeonMapWidget)

    init {
        DungeonDecorationHighlight
        on<WorldMapEvent> {
            if (PlayerDataCollector.location.isDungeon) {
                DungeonMapWidget.mapId = id
            }
        }
    }

}