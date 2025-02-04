package ru.dargen.evoplus.render.node.box

import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.hoverColor
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.util.math.v3

class DropElementsBoxNode : RectangleNode() {

    val top = +rectangle {
        size = v3(y = 15.0)

        color = Colors.Primary
        hoverColor = Colors.Primary.darker()

        leftClick { _, state ->
            if (isHovered && state) {
                dropped = !dropped
                true
            } else false
        }
    }

    val text = top + text("Elements") {
        align = Relative.LeftCenter
        origin = Relative.LeftCenter
        translation = v3(x = 5.0)
    }

    val selector = top + text("▼") {
        align = Relative.RightCenter
        origin = Relative.RightCenter
        translation = v3(x = -5.0)
    }

    val holder = +vbox {
        align = Relative.LeftBottom
        origin = Relative.LeftBottom

        dependSizeX = false
        fixChildSize = true
    }

    var dropped: Boolean = true
        set(value) {
            field = value
            holder.enabled = value
            selector.text = if (value) "▼" else "▲"
        }

    init {
        tick {
            top.size.x = size.x
            holder.size.x = size.x
            if (holder.enabled) size.y = top.size.y + holder.size.y
            else size.y = top.size.y
        }
    }

}