package dev.evoplus.feature.setting.gui.settings.input

import dev.evoplus.feature.setting.gui.ExpandingClickEffect
import dev.evoplus.feature.setting.gui.SettingPalette
import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.property.value.Bind
import dev.evoplus.feature.setting.utils.onLeftClick
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UKeyboard
import gg.essential.universal.USound

class BindComponent(bind: Bind, allowed: List<Bind.Type>) : SettingComponent() {

    private val container by UIBlock(SettingPalette.button).constrain {
        width = ChildBasedSizeConstraint() + 14.pixels
        height = ChildBasedSizeConstraint() + 8.pixels
    } childOf this

    private val text by UIWrappedText(
        "",
        trimText = true,
        shadowColor = SettingPalette.getTextShadow()
    ).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = width.coerceAtMost(300.pixels)
        height = 10.pixels
        color = SettingPalette.text.toConstraint()
    } childOf container

    private var intercepting = false

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        enableEffect(
            ExpandingClickEffect(
                SettingPalette.getPrimary().withAlpha(0.5f),
                scissorBoundingBox = container
            )
        )

        container.onMouseEnter {
            container.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.buttonHighlight.toConstraint())
            }
        }.onMouseLeave {
            container.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, SettingPalette.button.toConstraint())
            }
        }.onLeftClick {
            USound.playButtonPress()
            if (!intercepting) {
                intercepting = true
                grabWindowFocus()
                it.stopPropagation()
                text.setText("Ожидание...")
            }
        }

        if (Bind.Type.MOUSE in allowed) onMouseClick {
            if (intercepting) {
                rebind(Bind.Type.MOUSE, it.mouseButton)
                it.stopPropagation()
            }
        }
        container.onKeyType { _, key ->
            if (intercepting) {
                if (key == UKeyboard.KEY_ESCAPE) {
                    rebind(Bind.Type.NONE, -1)
                } else if (Bind.Type.KEYBOARD in allowed) {
                    rebind(Bind.Type.KEYBOARD, key)
                }
            }
        }

        text.setText(bind.buttonName)

    }

    fun rebind(type: Bind.Type, code: Int) {
        releaseWindowFocus()
        val bind = Bind(type, code)
        text.setText(bind.buttonName)
        intercepting = false
        changeValue(bind)
    }

}
