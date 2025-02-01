package dev.evoplus.feature.setting.property

import dev.evoplus.feature.setting.property.data.CategoryData
import dev.evoplus.feature.setting.property.data.DividerItem

data class Category(
    val id: String, val meta: PropertyMeta,
    val properties: Map<String, Property<*, *>>,
    val categories: Map<String, Category>,
) {

    val enabledProperties get() = properties.filterNot { it.value.meta.hidden }

    internal fun searchProperties(term: String) = enabledProperties.values.filter { it.meta.search(term) } +
            categories.values.flatMap { it.enabledProperties.values.filter { it.meta.search(term) } }

    internal fun createDividerItem() = DividerItem(meta)

    internal fun createItems(subscribe: Boolean = false) =
        enabledProperties.values.filter { subscribe || !it.meta.subscribe }.map(Property<*, *>::createItem)

    internal fun createData(subscribe: Boolean = false) = CategoryData(
        meta,
        createItems(subscribe) + categories.values.flatMap { listOf(it.createDividerItem()) + it.createItems(subscribe) }
    )

}