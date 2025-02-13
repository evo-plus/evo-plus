package dev.evoplus.feature.setting.gui.settings.color

import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.gui.settings.toggle.SwitchComponent
import dev.evoplus.feature.setting.property.value.SwitchColor
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import java.awt.Color


class SwitchColorComponent(state: Boolean, initial: Color, allowAlpha: Boolean) : SettingComponent() {

    val switch = SwitchComponent(state).constrain {
        x = 0.pixels(alignOpposite = true)
        y = CenterConstraint()
    } childOf this

    val picker = ColorComponent(initial, allowAlpha).constrain {
        x = 0.pixels()
        y = CenterConstraint()
    } childOf this

    init {
        constrain {
            width = 120.pixels
            height = ChildBasedMaxSizeConstraint()
        }
        switch.observe { callObserver() }
        picker.observe { callObserver() }
    }

    protected fun callObserver() {
        changeValue(SwitchColor(switch.enabled.get(), picker.getCurrentColor()))
    }

}
