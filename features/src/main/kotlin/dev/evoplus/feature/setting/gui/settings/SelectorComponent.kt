package dev.evoplus.feature.setting.gui.settings

import dev.evoplus.feature.setting.property.attr.SelectorPropertyAttr
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UI18n

class SelectorComponent(initialSelection: Int, attr: SelectorPropertyAttr<*>) : SettingComponent() {

    internal val dropDown by DropDownComponent(initialSelection, attr.optionsNames.map(UI18n::i18n)) childOf this

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = 17.pixels
        }

        dropDown.selectedIndex.onSetValue { changeValue(attr.valueOf(it)) }
    }

    override fun closePopups(instantly: Boolean) {
        dropDown.collapse(instantly)
    }

}
