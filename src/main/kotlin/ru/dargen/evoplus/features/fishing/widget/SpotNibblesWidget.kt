package ru.dargen.evoplus.features.fishing.widget

import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.protocol.registry.FishingSpot
import ru.dargen.evoplus.render.node.asyncTick
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.format.format

object SpotNibblesWidget : WidgetBase {

    override val node = text {
        isShadowed = true

        asyncTick {
            lines = FishingFeature.Nibbles
                .takeIf { FishingFeature.NibblesVisibleMode.isVisible()  }.orEmpty()
                .mapKeys { FishingSpot.valueOf(it.key) ?: return@asyncTick }
                .ifEmpty { if (isWidgetEditor) FishingSpot.values.take(5).associateWith { 100.0 } else emptyMap() }
                .map { (spot, nibble) -> "§e${spot.name} §7- §6${nibble.format("###.#")}%" }
        }
    }

}