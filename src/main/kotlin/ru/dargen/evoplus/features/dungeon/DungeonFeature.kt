package ru.dargen.evoplus.features.dungeon

import net.minecraft.item.Items
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.WorldMapEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.vigilant.FeatureCategory
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.util.minecraft.customItem

object DungeonFeature : Feature("dungeon", "Данжи", customItem(Items.PAPER, 14)) {

    var DecorationHighlight = true
    val Map by widgets.widget("Карта", widget = DungeonMapWidget)

    override fun FeatureCategory.setup() {
        switch(::DecorationHighlight, "Подсветка декораций",
            "Подсвечивает разрушаемые декорации в данже")
    }

    init {
        DungeonDecorationHighlight
        on<WorldMapEvent> {
            if (PlayerDataCollector.location.isDungeon) {
                DungeonMapWidget.mapId = id
            }
        }
    }
}