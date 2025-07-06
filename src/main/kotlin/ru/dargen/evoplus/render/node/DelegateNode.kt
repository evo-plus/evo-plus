package ru.dargen.evoplus.render.node

import net.minecraft.client.gui.DrawContext

class DelegateNode : Node() {

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        children.forEach { it.size = size.clone() }
    }

}

fun delegate(block: DelegateNode.() -> Unit = {}) = DelegateNode().apply(block)