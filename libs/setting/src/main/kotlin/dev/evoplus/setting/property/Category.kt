package dev.evoplus.setting.property

import dev.evoplus.setting.property.data.CategoryData
import dev.evoplus.setting.property.data.DividerItem

data class Category(
    val id: String, val meta: PropertyMeta,
    val properties: Map<String, Property<*>>,
    val categories: Map<String, Category>,
) {

    val enabledProperties get() = properties.filterNot { it.value.meta.hidden }

    internal fun searchProperties(term: String) = enabledProperties.values.filter { it.meta.search(term) } +
            categories.values.flatMap { it.enabledProperties.values.filter { it.meta.search(term) } }

    internal fun createDividerItem() = DividerItem(meta)

    internal fun createItems() = enabledProperties.values.map(Property<*>::createItem)

    internal fun createData() = CategoryData(
        meta, createItems() + categories.values.flatMap { listOf(it.createDividerItem()) + it.createItems() }
    )

}