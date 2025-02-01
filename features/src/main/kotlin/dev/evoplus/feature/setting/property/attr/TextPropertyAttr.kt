package dev.evoplus.feature.setting.property.attr


/**
 * For [PropertyType.Text], [PropertyType.Paragraph], and [PropertyType.Button]
 */
data class TextPropertyAttr(
    val placeholder: String = "",
    val protected: Boolean = false,
)