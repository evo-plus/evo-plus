package ru.dargen.evoplus.features.fishing.widget

import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.protocol.registry.FishingSpot
import ru.dargen.evoplus.util.format.format
import ru.dargen.evoplus.util.math.v3

object SpotNibblesWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._children = FishingFeature.Nibbles
            .takeIf { !FishingFeature.NibblesOnlyOnFish || PlayerDataCollector.location.id == "fish" }
            ?.mapKeys { FishingSpot.valueOf(it.key) ?: return }
            ?.ifEmpty { if (isWidgetEditor) FishingSpot.values.take(5).associateWith { 100.0 } else emptyMap() }
            .orEmpty()
            .map { (spot, nibble) ->
                hbox {
                    space = 1.0
                    indent = v3()

                    +text("§e${spot.name} §7- §6${nibble.format("###.#")}%") { isShadowed = true }
                    recompose()
                }
            }.toMutableList()
    }

}