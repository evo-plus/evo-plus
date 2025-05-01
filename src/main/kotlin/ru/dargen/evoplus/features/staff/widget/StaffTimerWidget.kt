package ru.dargen.evoplus.features.staff.widget

import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.staff.StaffFeature
import ru.dargen.evoplus.protocol.registry.StaffType
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3

object StaffTimerWidget : WidgetBase {

    override val node = hbox {
        space = 2.0
        indent = v3()
    }

    fun update() {
        node._childrens = StaffType.Companion.values
            .associateWith { StaffFeature.Staffs[it.id] ?: 0L }
            .filterValues { StaffFeature.Staffs.isEmpty() && isWidgetEditor || it > currentMillis }
            .map { (type, timestamp) ->
                val remainTime = (timestamp - currentMillis).coerceAtLeast(0L)

                item(type.displayItem) {
                    +text("§e${remainTime / 1000}") {
                        translation = v3(1.0, 1.0, 200.0)
                        scale = v3(.9, .9, .9)
                        origin = Relative.RightBottom
                        align = Relative.RightBottom
                    }
                }
            }.toMutableList()
    }

    override fun Node.prepare() {
        origin = Relative.LeftBottom
        align = v3(.005, 1.0)
    }

}