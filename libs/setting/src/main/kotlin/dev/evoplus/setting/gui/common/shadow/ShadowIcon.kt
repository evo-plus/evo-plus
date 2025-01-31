package dev.evoplus.setting.gui.common.shadow

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.toConstraint
import dev.evoplus.setting.gui.SettingPalette
import dev.evoplus.setting.gui.common.AutoImageSize
import dev.evoplus.setting.utils.ImageFactory
import dev.evoplus.setting.utils.hoveredState
import dev.evoplus.setting.utils.onSetValueAndNow
import java.awt.Color

internal class ShadowIcon(
    imageFactory: State<ImageFactory>,
    buttonShadow: State<Boolean>,
    primaryColor: State<Color>,
    shadowColor: State<Color>,
) : UIContainer() {

    private val iconState = imageFactory.map { it }
    private val buttonShadowState = buttonShadow.map { it }
    private val primaryColorState = primaryColor.map { it }
    private val shadowColorState = shadowColor.map { it }

    constructor(imageFactory: ImageFactory, buttonShadow: Boolean) : this(
        BasicState(imageFactory),
        BasicState(buttonShadow)
    )

    constructor(
        imageFactory: State<ImageFactory>,
        buttonShadow: State<Boolean>
    ) : this(
        imageFactory,
        buttonShadow,
        BasicState(Color.BLACK), // can't access hover state until after constructor call
        SettingPalette.textShadow
    ) {
        rebindPrimaryColor(SettingPalette.getTextColor(hoveredState()))
    }

    init {

        iconState.zip(buttonShadowState).onSetValueAndNow { (icon, shadow) ->
            clearChildren()
            val image = icon.create().constrain {
                width = 100.percent
                height = 100.percent
            }.also {
                it.supply(AutoImageSize(this@ShadowIcon))
            }.setColor(primaryColorState.toConstraint()) childOf this@ShadowIcon

            if(shadow) {
                image effect ShadowEffect().rebindColor(shadowColorState)
            }

        }
    }

    fun rebindShadowColor(color: State<Color>) = apply {
        shadowColorState.rebind(color)
    }

    fun rebindPrimaryColor(color: State<Color>) = apply {
        primaryColorState.rebind(color)
        return this
    }

    fun rebindIcon(imageFactory: State<ImageFactory>) = apply {
        iconState.rebind(imageFactory)
    }

    fun rebindShadow(shadow: State<Boolean>) = apply {
        buttonShadowState.rebind(shadow)
    }
}
