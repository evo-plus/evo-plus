package ru.dargen.evoplus.feature.screen

import ru.dargen.evoplus.render.node.DummyNode
import ru.dargen.evoplus.render.node.Node

fun interface FeatureScreenElement {

    data object Dummy : FeatureScreenElement {
        override fun create() = DummyNode
    }

    fun create(): Node

}