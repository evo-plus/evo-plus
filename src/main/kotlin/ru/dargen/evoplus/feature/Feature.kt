package ru.dargen.evoplus.feature

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.group.FeatureSettings
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.minecraft.itemStack

abstract class Feature(id: String, name: String, val icon: ItemStack) :
    FeatureCategory(id, name, FeatureSettings(id, name)), FeatureElement {
    constructor(id: String, name: String, icon: Item) : this(id, name, itemStack(icon))

    val widgets = WidgetGroup().apply(settings.value::add)

    fun category(category: FeatureCategory) = settings.setting(category.settings)

    override fun createElement(prompt: FeaturePrompt) = settings.createElement(prompt)

    override fun search(prompt: FeaturePrompt) = prompt.shouldPass(name) || settings.search(prompt)

}