package ru.dargen.evoplus.features.dungeon

import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.mixin.render.map.MapRendererAccessor
import ru.dargen.evoplus.mixin.render.map.MapTextureAccessor
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.node.preTransform
import ru.dargen.evoplus.render.node.texture
import ru.dargen.evoplus.render.node.tick
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.kotlin.invoke
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client

object DungeonMapWidget : WidgetBase {

    var mapId = -1

    override val node = texture {
        size = v3(128.0, 128.0)
        textureOffset = v3(1.0, 1.0)
        textureSize = v3(126.0, 126.0)

        tick {
            enabled = isWidgetEditor || PlayerDataCollector.location.isProceduralDungeon && mapId != -1
        }

        preTransform { matrices, tickDelta ->
            if (mapId == -1) return@preTransform

            texture = Client.gameRenderer!!.mapRenderer<MapRendererAccessor>()!!.textures.get(mapId)
                ?.cast<MapTextureAccessor>()?.texture?.glId ?: -1
        }

    }

}