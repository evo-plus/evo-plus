package dev.evoplus.setting.gui.settings

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import dev.evoplus.setting.gui.DataBackedSetting

abstract class SettingComponent : UIContainer() {

    protected var observer: (Any?) -> Unit = {}
    protected var lastValue: Any? = null

    init {
        constrain {
            x = (DataBackedSetting.INNER_PADDING + 10f).pixels(alignOpposite = true)
            y = CenterConstraint()
        }
    }

    fun observe(listener: (Any?) -> Unit) {
        this.observer = listener
    }

    fun changeValue(newValue: Any?, observe: Boolean = true) {
        if (newValue != lastValue) {
            lastValue = newValue
            if (observe) {
                observer(newValue)
            }
        }
    }

    open fun closePopups(instantly: Boolean = false) { }

    open fun setupParentListeners(parent: UIComponent) { }

    companion object {
        const val DOWN_ARROW_PNG = "/assets/evo-plus/textures/gui/settings/arrow-down.png"
        const val UP_ARROW_PNG = "/assets/evo-plus/textures/gui/settings/arrow-up.png"
    }
}
