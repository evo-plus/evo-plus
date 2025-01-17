package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.node.DummyNode
import ru.dargen.evoplus.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class FeatureScreenElements {

    val elements = mutableSetOf<FeatureScreenElement>()
    val elementsSection
        get() = vScrollView {
            box.color = Colors.TransparentBlack
            addElements(this@FeatureScreenElements.elements.map(FeatureScreenElement::create).filter { it !== DummyNode })
        }

    fun element(element: FeatureScreenElement) = elements.add(element)

    fun baseElement(name: String, element: FeatureScreenElement) = element(FeatureBaseElement(name, element))

}