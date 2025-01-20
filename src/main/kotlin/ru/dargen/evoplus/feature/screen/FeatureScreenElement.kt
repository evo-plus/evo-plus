package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.node.Node

fun interface FeatureScreenElement {

    fun create(prompt: FeaturePrompt): Node

    fun search(prompt: FeaturePrompt) = false

}