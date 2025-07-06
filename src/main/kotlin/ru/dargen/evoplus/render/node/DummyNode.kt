package ru.dargen.evoplus.render.node

import net.minecraft.client.gui.DrawContext

data object DummyNode : Node() {

    override var enabled: Boolean
        get() = false
        set(value) {}

    override fun renderElement(context: DrawContext, tickDelta: Float) {}

}
