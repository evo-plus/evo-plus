package ru.dargen.evoplus.render.node

import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class ItemStackNode(var item: ItemStack = ItemStack(Items.AIR)) : RectangleNode() {

    init {
        size = v3(16.0, 16.0)
    }

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        super.renderElement(context, tickDelta)
        context.drawItem(item, 0, 0)
    }

}

fun item(itemStack: ItemStack = ItemStack(Items.AIR), block: ItemStackNode.() -> Unit = {}) =
    ItemStackNode(itemStack).apply(block)