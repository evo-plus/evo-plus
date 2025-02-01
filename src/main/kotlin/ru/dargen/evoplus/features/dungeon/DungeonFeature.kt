package ru.dargen.evoplus.features.dungeon

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.WorldMapEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector

object DungeonFeature : Feature("dungeon", "Данжи") {

    var DecorationHighlight = true
    val Map by widgets.widget("Карта", widget = DungeonMapWidget)

    override fun CategoryBuilder.setup() {
        switch(::DecorationHighlight, "Подсветка декораций",
            "Подсвечивает разрушаемые декорации в данже")
    }

    override fun initialize() {
        DungeonDecorationHighlight
        on<WorldMapEvent> {
            if (PlayerDataCollector.location.isDungeon) {
                DungeonMapWidget.mapId = id
            }
        }
    }
}