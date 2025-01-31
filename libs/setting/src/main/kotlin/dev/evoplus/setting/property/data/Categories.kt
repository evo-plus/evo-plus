package dev.evoplus.setting.property.data

import dev.evoplus.setting.gui.*
import dev.evoplus.setting.property.Property
import dev.evoplus.setting.property.PropertyMeta

open class CategoryData(val meta: PropertyMeta, val items: List<CategoryItem>) {

    val isNotEmpty get() = items.filter { it !is DividerItem }.isNotEmpty()

    override fun toString() = "Category \"${meta.name}\"\n ${items.joinToString(separator = "\n") { "\t$it" }}"

    data object Empty : CategoryData(PropertyMeta("", ""), emptyList())

}

sealed class CategoryItem {

    abstract fun toSettingsObject(): Setting?

}

class DividerItem(val meta: PropertyMeta) : CategoryItem() {

    override fun toSettingsObject() = Divider(meta.name, if (meta.hasDescription) meta.localizedDescription else null)

    override fun toString() = "Divider \"${meta.name}\""

}

class PropertyItem(val data: Property<*>) : CategoryItem() {

    override fun toSettingsObject() = DataBackedSetting(data, data.createComponent())

    override fun toString() = "${data.type} \"${data.id}\""

}
