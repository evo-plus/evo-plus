package ru.dargen.evoplus.feature

import gg.essential.vigilance.Vigilant.CategoryPropertyBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.group.SettingGroup
import ru.dargen.evoplus.feature.vigilant.FeaturesVigilant
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.minecraft.printMessage

typealias Category = CategoryPropertyBuilder

@KotlinOpens
abstract class Feature(
    val id: String, val name: String,
    /*var description: String? = null, */val icon: ItemStack = itemStack(Items.AIR),
) : FeatureElement {
    constructor(id: String, name: String/*, description: String? = null*/, icon: Item = Items.AIR) :
            this(id, name/*, description*/, itemStack(icon))

    val settings: SettingGroup = SettingGroup(id, name)
    val widgets = WidgetGroup().apply(settings.value::add)

    protected /*abstract*/ fun Category.setup() {}

    final fun setupInternal(category: Category?) {
        category?.subcategory(name) {
            setup()
        } ?: FeaturesVigilant.category(name) {
            setup()
            //TODO: remove after migration
            button("Test") {
                printMessage("Feature called $name")
            }
        }
    }

    final inline fun <reified T> config(name: String = id, value: T) = Features.config(name, value)

    protected fun Category.subcategory(category: ru.dargen.evoplus.feature.Feature) = category.setupInternal(this)

    override fun search(prompt: FeaturePrompt) = TODO()
    override fun createElement(prompt: FeaturePrompt) = TODO()

}