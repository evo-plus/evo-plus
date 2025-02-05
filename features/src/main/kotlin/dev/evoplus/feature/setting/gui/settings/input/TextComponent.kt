package dev.evoplus.feature.setting.gui.settings.input

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.toConstraint
import dev.evoplus.feature.setting.gui.SettingPalette
import dev.evoplus.feature.setting.gui.common.input.AbstractTextInput
import dev.evoplus.feature.setting.gui.common.input.UIMultilineTextInput
import dev.evoplus.feature.setting.gui.common.input.UIPasswordInput
import dev.evoplus.feature.setting.gui.common.input.UITextInput
import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.utils.onLeftClick

class TextComponent(
    private val initial: String,
    placeholder: String,
    wrap: Boolean,
    protected: Boolean
) : SettingComponent() {

    private val textHolder = UIBlock().constrain {
        width = ChildBasedSizeConstraint() + (if (protected) 14 else 6).pixels
        height = ChildBasedSizeConstraint() + 6.pixels
        color = SettingPalette.dividerDark.toConstraint()
    } childOf this effect OutlineEffect(SettingPalette.getComponentBorder(), 1f).bindColor(SettingPalette.componentBorder)

    private val textInput: AbstractTextInput = when {
        wrap -> UIMultilineTextInput(placeholder = placeholder).setMaxLines(10).constrain {
            width = basicWidthConstraint { this@TextComponent.parent.getWidth() * 0.4f }
        }

        protected -> UIPasswordInput(placeholder = placeholder).setMinWidth(50.pixels)
            .setMaxWidth(basicWidthConstraint { this@TextComponent.parent.getWidth() * 0.5f })

        else -> UITextInput(placeholder = placeholder).setMinWidth(50.pixels)
            .setMaxWidth(basicWidthConstraint { this@TextComponent.parent.getWidth() * 0.5f })
    }.constrain {
        x = 3.pixels
        y = 3.pixels
        color = SettingPalette.text.toConstraint()
    }

    private var hasSetInitialText = false

    init {
        textInput childOf textHolder
        textInput.onUpdate { newText ->
            changeValue(newText)
        }.onLeftClick { event ->
            event.stopPropagation()

            textInput.grabWindowFocus()
        }.onFocus {
            textInput.setActive(true)
        }.onFocusLost {
            textInput.setActive(false)
        }

        if (protected) {
            var toggle = false
            UIImage.ofResourceCached("/assets/evo-plus/textures/gui/settings/eye.png").constrain {
                y = 3.pixels
                x = 3.pixels(alignOpposite = true)
                width = 12.pixels
                height = basicHeightConstraint { textInput.getHeight() }
                color = SettingPalette.textDisabled.toConstraint()
            }.onMouseEnter {
                if (!toggle) {
                    animate {
                        setColorAnimation(Animations.OUT_EXP, .2f, SettingPalette.text.toConstraint())
                    }
                }
            }.onMouseLeave {
                if (!toggle) {
                    animate {
                        setColorAnimation(Animations.OUT_EXP, .2f, SettingPalette.textDisabled.toConstraint())
                    }
                }
            }.onLeftClick {
                toggle = !toggle
                (textInput as UIPasswordInput).setProtection(!toggle)
                animate {
                    setColorAnimation(Animations.OUT_EXP, .2f, if (toggle) {
                        SettingPalette.textWarning
                    } else {
                        SettingPalette.text
                    }.toConstraint())
                }
            } childOf textHolder

            textHolder.setHeight(basicHeightConstraint { textInput.getHeight() + 6f })
        }

        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
    }

    override fun animationFrame() {
        super.animationFrame()

        if (!hasSetInitialText) {
            textInput.setText(initial)
            hasSetInitialText = true
        }
    }

    override fun closePopups(instantly: Boolean) {
        textInput.setActive(false)
    }
}
