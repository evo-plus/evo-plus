package dev.evoplus.feature.setting.gui.settings.slider

import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint
import dev.evoplus.feature.setting.gui.SettingPalette
import java.util.*

class DecimalSliderComponent(value: Float, min: Float, max: Float, places: Int = 1) : AbstractSliderComponent() {

    init {
        UIText(min.toString()).constrain {
            y = CenterConstraint()
            color = SettingPalette.text.toConstraint()
        } childOf this
    }

    override val slider by Slider((value - min) / (max - min)).constrain {
        x = SiblingConstraint()
        width = 60.pixels
        height = 12.pixels
    } childOf this

    init {
        UIText(max.toString()).constrain {
            x = SiblingConstraint()
            y = CenterConstraint()
            color = SettingPalette.text.toConstraint()
        } childOf this
    }

    private val currentValueText by UIText(value.toString()).constrain {
        x = CenterConstraint() boundTo slider.grabBox
        y = 150.percent
        color = SettingPalette.text.toConstraint()
    } childOf slider

    init {
        slider.onValueChange { newPercentage ->
            val newValue = "%.${places}f".format(Locale.US,min + (newPercentage * (max - min)))
            changeValue(newValue.toFloat())
            currentValueText.setText(newValue)
        }

        sliderInit()
    }
}
