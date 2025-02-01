package dev.evoplus.feature.setting.gui

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint
import gg.essential.universal.USound
import dev.evoplus.feature.setting.gui.elementa.GuiScaleOffsetConstraint
import dev.evoplus.feature.setting.property.data.CategoryData
import dev.evoplus.feature.setting.utils.onLeftClick

class CategoryLabel(private val gui: SettingsGui, private val category: CategoryData) : UIContainer() {

    private val text by UIText(category.meta.name, shadowColor = SettingPalette.getTextShadowMid()).constrain {
        y = CenterConstraint()
        textScale = GuiScaleOffsetConstraint(1f)
        color = SettingPalette.text.toConstraint()
    } childOf this

    var isSelected = false

    init {
        constrain {
            y = SiblingConstraint()
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint() + 8.pixels
        }

        onLeftClick {
            if (!isSelected) {
                USound.playButtonPress()
                select()
            }
        }

        onMouseEnter {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.textHighlight.toConstraint())
                }.setShadowColor(SettingPalette.getTextShadowMid())
            }
        }

        onMouseLeave {
            if (!isSelected) {
                text.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.text.toConstraint())
                }.setShadowColor(SettingPalette.getTextShadowMid())
            }
        }
    }

    fun select() {
        gui.selectCategory(category)

        isSelected = true
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.textActive.toConstraint())
        }.setShadowColor(SettingPalette.getTextActiveShadow())
    }

    fun deselect() {
        isSelected = false
        text.animate {
            setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.text.toConstraint())
        }.setShadowColor(SettingPalette.getTextShadowMid())
    }
}
