package dev.evoplus.feature.setting.gui.settings.slider

import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.utils.onLeftClick
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.USound

abstract class AbstractSliderComponent : SettingComponent() {

    protected abstract val slider: Slider
    private var expanded = false
    private var mouseHeld = false

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedMaxSizeConstraint()
        }
    }

    protected fun sliderInit() {
        onLeftClick {
            USound.playButtonPress()
            mouseHeld = true
        }
        onMouseRelease {
            mouseHeld = false
            if (expanded && !slider.isHovered()) {
                slider.animate {
                    setWidthAnimation(Animations.OUT_EXP, .25f, 60.pixels())
                }
                expanded = false
            }
        }
    }

    fun incrementBy(inc: Float) {
        slider.setCurrentPercentage(slider.getCurrentPercentage() + inc)
    }
}
