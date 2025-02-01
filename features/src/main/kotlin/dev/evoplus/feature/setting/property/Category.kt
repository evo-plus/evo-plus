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

    internal fun createItems(subscription: Boolean = false) =
        enabledProperties.values.filter { subscription || !it.meta.subscription }.map(Property<*, *>::createItem)

    internal fun createData(subscription: Boolean = false) = CategoryData(
        meta,
        createItems(subscription) + categories.values.flatMap { listOf(it.createDividerItem()) + it.createItems(subscription) }
    )

}