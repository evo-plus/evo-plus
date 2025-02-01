package ru.dargen.evoplus.feature

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.group.SettingGroup
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.minecraft.itemStack


@KotlinOpens
abstract class Feature(
    val id: String, val name: String,
    /*var description: String? = null, */val icon: ItemStack = itemStack(Items.AIR),
) : FeatureElement {
    constructor(id: String, name: String/*, description: String? = null*/, icon: Item = Items.AIR) :
            this(id, name/*, description*/, itemStack(icon))

    val settings: SettingGroup = SettingGroup(id, name)
    val widgets = WidgetGroup().apply(settings.value::add)

    protected fun CategoryBuilder.setup() {}

    fun preInitialize() {}

    fun initialize() {}

    final fun setupInternal(category: CategoryBuilder?) {
        category?.subcategory(id, name) { setup() } ?: FeaturesSettings.category(id, name) { setup() }
    }

    final inline fun <reified T> config(name: String = id, value: T) = Features.config(name, value)

    protected fun CategoryBuilder.subcategory(category: Feature) = category.setupInternal(this)

    override fun search(prompt: FeaturePrompt) = true
    override fun createElement(prompt: FeaturePrompt) = settings.createElement(prompt)

}