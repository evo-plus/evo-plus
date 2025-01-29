package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.node.Node

fun interface FeatureElement {

    fun createElement(prompt: FeaturePrompt): Node

    fun search(prompt: FeaturePrompt) = false

}