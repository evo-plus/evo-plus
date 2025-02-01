package dev.evoplus.feature.setting.gui.settings

import dev.evoplus.feature.setting.gui.ExpandingClickEffect
import dev.evoplus.feature.setting.gui.SettingPalette
import dev.evoplus.feature.setting.utils.onLeftClick
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.State
import gg.essential.elementa.state.toConstraint
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UI18n
import gg.essential.universal.USound

class ButtonComponent(placeholder: String, action: () -> Unit) : SettingComponent() {

    private var textState: State<String> = BasicState(placeholder)
    private var listener: () -> Unit = textState.onSetValue { text.setText(textState.get()) }

    private val container by UIBlock(SettingPalette.button).constrain {
        width = ChildBasedSizeConstraint() + 14.pixels
        height = ChildBasedSizeConstraint() + 8.pixels
    } childOf this

    private val text by UIWrappedText(
        textState.get(),
        trimText = true,
        shadowColor = SettingPalette.getTextShadow()
    ).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = width.coerceAtMost(300.pixels)
        height = 10.pixels
        color = SettingPalette.text.toConstraint()
    } childOf container

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
            action()
        }
    }

    fun bindText(newTextState: State<String>) = apply {
        listener()
        textState = newTextState
        text.bindText(textState.map(UI18n::i18n))

        listener = textState.onSetValue {
            text.setText(textState.get())
        }
    }

    fun getText() = textState.get()
    fun setText(text: String) = apply { textState.set(text) }

}
