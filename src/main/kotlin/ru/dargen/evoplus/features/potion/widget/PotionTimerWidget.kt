package ru.dargen.evoplus.features.potion.widget

import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.potion.PotionFeature
import ru.dargen.evoplus.features.potion.PotionState
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3

object PotionTimerWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._childrens = PotionFeature.ComparedPotionsTimers
            .take(PotionFeature.PotionsCount)
            .associate { it.key to it.value }
            .ifEmpty {
                if (isWidgetEditor) PotionType.values.take(5).associateWith { PotionState(55, 2000) }
                else emptyMap()
            }.map { (potionType, potionState) ->
                val (quality, endTime) = potionState
                val remainTime = endTime - currentMillis

                hbox {
                    space = 1.0
                    indent = v3()

                    +item(potionType.displayItem) { scale = scale(.7, .7) }
                    +text("${potionType.displayName} ($quality%)§8:§f ${remainTime.asShortTextTime}") { isShadowed = true }

                    recompose()
                }
            }.toMutableList()
    }

    override fun Node.prepare() {
        align = Relative.LeftCenter + v3(y = .03)
        origin = Relative.LeftCenter
    }

}