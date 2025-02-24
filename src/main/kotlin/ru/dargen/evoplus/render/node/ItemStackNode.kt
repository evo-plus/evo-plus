package ru.dargen.evoplus.render.node

import net.minecraft.client.render.model.json.ModelTransformationMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.ItemRenderer

@KotlinOpens
class ItemStackNode(var item: ItemStack = ItemStack(Items.AIR)) : RectangleNode() {

    init {
        size = v3(16.0, 16.0)
    }

    override fun renderElement(matrices: MatrixStack, tickDelta: Float) {
        super.renderElement(matrices, tickDelta)
        ItemRenderer.renderItem(item, ModelTransformationMode.NONE, false, matrices, null, 0, 0, null)
    }

}

fun item(itemStack: ItemStack = ItemStack(Items.AIR), block: ItemStackNode.() -> Unit = {}) =
    ItemStackNode(itemStack).apply(block)