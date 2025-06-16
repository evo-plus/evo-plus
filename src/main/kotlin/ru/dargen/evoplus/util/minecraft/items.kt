package ru.dargen.evoplus.util.minecraft

import net.minecraft.component.ComponentMap
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import net.minecraft.util.Identifier

val Item.identifier get() = Registries.ITEM.getId(this).path

fun itemOf(path: String) = Registries.ITEM.get(Identifier.of("minecraft", path.lowercase()))

fun itemStack(type: Item, block: ItemStack.() -> Unit = {}) = ItemStack(type).apply(block)

fun customItem(type: Item, customModelData: Int, block: ItemStack.() -> Unit = {}) =
    itemStack(type).apply { this.customModelData = customModelData }.apply(block)

fun ItemStack.equalCustomModel(item: ItemStack) = this.item == item.item && customModelData == item.customModelData

fun ItemStack.editComponents(map: ComponentMap.Builder.() -> Unit) =
    applyComponentsFrom(ComponentMap.builder().addAll(components).apply(map).build())

var ItemStack.customModelData: Int?
    get() = components.get(DataComponentTypes.CUSTOM_MODEL_DATA)?.value
    set(value) {
        if (value != null) editComponents {
            set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent(value))
        }
    }

var ItemStack.displayName: Text?
    get() = name
    set(value) {
        editComponents {
            set(DataComponentTypes.CUSTOM_NAME, value ?: Text.empty())
        }
    }

var ItemStack.lore: List<Text>
    get() = components.get(DataComponentTypes.LORE)
        ?.lines?.toMutableList() ?: mutableListOf()
    set(value) {
        editComponents {
            set(DataComponentTypes.LORE, LoreComponent(value))
        }
    }

