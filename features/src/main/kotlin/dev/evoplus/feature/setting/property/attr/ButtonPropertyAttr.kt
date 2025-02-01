package dev.evoplus.feature.setting.property.attr

import gg.essential.universal.UI18n

data class ButtonPropertyAttr(val text: String, val action: () -> Unit) {

    val localizedText get() = UI18n.i18n(text)

}
