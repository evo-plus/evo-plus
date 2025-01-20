package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.node.DummyNode
import ru.dargen.evoplus.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class FeatureScreenElements {

    val elements = mutableSetOf<FeatureScreenElement>()

    fun lookupSection(prompt: FeaturePrompt) = vScrollView {
        box.color = Colors.TransparentBlack

        addElements(
            this@FeatureScreenElements.elements
                .filter { it.search(prompt) }
                .map { it.create(prompt)}
                .filter { it !== DummyNode }
        )
    }

    fun searchInSection(prompt: FeaturePrompt) = elements.any { it.search(prompt) }

    fun element(element: FeatureScreenElement) = elements.add(element)

    fun baseElement(name: String, element: FeatureScreenElement) = element(FeatureBaseElement(name, element))

}