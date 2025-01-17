package ru.dargen.evoplus.render

import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.hover
import java.awt.Color

//TODO: make better
var Node.hoverColor: Color
    get() = Colors.Transparent
    set(hoverColor) {
        val color = this.color
        hover { _, state ->
            animate("hover", .1) {
                this@hoverColor.color = if (state) hoverColor else color
            }
        }
    }