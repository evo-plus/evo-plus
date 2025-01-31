package dev.evoplus.setting.property.attr

import gg.essential.universal.UI18n

data class ButtonPropertyAttr(val text: String) {

    val localizedText get() = UI18n.i18n(text)

}
