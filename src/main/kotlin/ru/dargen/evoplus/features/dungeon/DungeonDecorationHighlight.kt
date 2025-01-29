package ru.dargen.evoplus.features.dungeon

import ru.dargen.evoplus.feature.render.highligh.DisplayHighlighter
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.Colors

object DungeonDecorationHighlight : DisplayHighlighter(DungeonFeature::DecorationHighlight) {

    override val color = Colors.Yellow
    override val ids = (10271..10282) + (10311..10327)

    override fun shouldProcess() = PlayerDataCollector.location.isDungeon

}