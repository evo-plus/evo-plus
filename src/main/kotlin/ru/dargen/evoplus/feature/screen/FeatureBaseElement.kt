package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.rectangle
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class FeatureBaseElement(val name: String, val element: FeatureScreenElement) : FeatureScreenElement {

    override fun create(prompt: FeaturePrompt) = rectangle {
        color = Colors.TransparentBlack
        size = v3(y = 30.0)
        +text(prompt.highlightPrompt(name)) {
            translation = v3(x = 5.0)
            align = Relative.LeftCenter
            origin = Relative.LeftCenter
        }
        +element.create(prompt).apply {
            translation = v3(x = -5.0)
            align = Relative.RightCenter
            origin = Relative.RightCenter
        }
    }

    override fun search(prompt: FeaturePrompt) = prompt.shouldPass(this.name)

}