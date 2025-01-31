package dev.evoplus.setting.gui.settings

import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.AspectConstraint
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.toConstraint
import gg.essential.universal.USound
import dev.evoplus.setting.gui.SettingPalette
import dev.evoplus.setting.utils.onLeftClick

class CheckboxComponent(initialValue: Boolean) : SettingComponent() {

    var checked: Boolean = initialValue
        set(value) {
            changeValue(value)
            field = value
        }

    private val checkmark = UIImage.ofResourceCached("/assets/evo-plus/textures/gui/settings/check.png").constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 16.pixels
        height = 12.pixels
        color = SettingPalette.primary.toConstraint()
    } childOf this

    init {
        constrain {
            width = 20.pixels
            height = AspectConstraint()
        }

        effect(getOutlineEffect())

        if (!checked)
            checkmark.hide(instantly = true)

        onLeftClick {
            USound.playButtonPress()
            checked = !checked

            removeEffect<OutlineEffect>()
            effect(getOutlineEffect())

            if (checked) {
                checkmark.unhide()
            } else {
                checkmark.hide()
            }
        }
    }

    private fun getOutlineEffect() = OutlineEffect(getSettingColor().get(), 1f).bindColor(getSettingColor())

    private fun getSettingColor() = if (checked) SettingPalette.primary else SettingPalette.componentBorder
}
