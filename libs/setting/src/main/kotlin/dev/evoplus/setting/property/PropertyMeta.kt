package dev.evoplus.setting.property

import gg.essential.universal.UI18n

data class PropertyMeta(
    val name: String, val description: String? = null,
    val hidden: Boolean = false, val subscribe: Boolean = false,
) {

    val searchTags = emptyList<String>()

    val localizedName get() = UI18n.i18n(name)
    val localizedDescription get() = description?.let(UI18n::i18n) ?: ""

    val hasDescription get() = !description.isNullOrBlank()

    fun search(term: String) = localizedName.search(term) || localizedDescription.search(term) || searchTags.any { it.search(term) }

    private fun String?.search(term: String) = this?.contains(term, ignoreCase = true) == true

}