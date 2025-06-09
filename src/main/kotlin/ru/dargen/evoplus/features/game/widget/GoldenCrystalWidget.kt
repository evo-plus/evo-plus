package ru.dargen.evoplus.features.game.widget

import net.minecraft.client.render.DiffuseLighting
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.features.game.GoldenCrystalItem
import ru.dargen.evoplus.features.game.GoldenRushFeature.GoldenCrystalIndicatorText
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.postTransform
import ru.dargen.evoplus.render.node.preTransform
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3

object GoldenCrystalWidget : WidgetBase {


    val MainBox = hbox {
        indent = v3()
        space = 3.0

        +GoldenCrystalIndicatorText

        +item(GoldenCrystalItem) {
            scale = scale(.5, .5, .5)
            rotation = v3(y = 50.0)
            translation = v3(x = -20.0)

            preTransform { matrices, tickDelta -> DiffuseLighting.disableGuiDepthLighting() }
            postTransform { matrices, tickDelta -> DiffuseLighting.enableGuiDepthLighting() }
        }
    }

    override val node = vbox {
        align = v3(.95, .26)

        +MainBox
    }

    override fun Node.prepare() {
        origin = Relative.RightTop
        align = v3(.0, 1.99)
    }

}