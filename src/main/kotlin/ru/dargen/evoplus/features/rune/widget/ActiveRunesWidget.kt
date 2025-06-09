package ru.dargen.evoplus.features.rune.widget

import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.math.v3

object ActiveRunesWidget : WidgetBase {

    val ActiveRunesText = text(
        " §e??? ???", " §6??? ???",
        " §6??? ???", " §a??? ???", " §a??? ???"
    ) { isShadowed = true }

    val MainBox = hbox {
        align = v3(0.25)
        origin = Relative.CenterTop

        +ActiveRunesText
    }

    fun update(text: String) {
        ActiveRunesText.text = text
    }

    override val node = vbox {
        space = .0
        indent = v3()

        +MainBox
    }

    override fun Node.prepare() {
        origin = Relative.CenterBottom
        align = v3(.03, .55)
    }

}