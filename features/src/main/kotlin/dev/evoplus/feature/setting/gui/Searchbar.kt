package dev.evoplus.feature.setting.gui

import dev.evoplus.feature.setting.gui.common.IconButton
import dev.evoplus.feature.setting.gui.common.input.UITextInput
import dev.evoplus.feature.setting.gui.common.shadow.ShadowIcon
import dev.evoplus.feature.setting.utils.bindParent
import dev.evoplus.feature.setting.utils.not
import dev.evoplus.feature.setting.utils.onLeftClick
import dev.evoplus.feature.setting.utils.state
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UKeyboard

internal class Searchbar(
    placeholder: String = "Поиск...",
    initialValue: String = "",
    private val activateOnSearchHokey: Boolean = true,
    private val activateOnType: Boolean = false,
    private val expandedWidth: Int = 104,
) : UIContainer() {

    private val collapsed = BasicState(true)
    val textContent = BasicState(initialValue)

    private val toggleIcon = collapsed.map {
        if (it) {
            SettingPalette.SEARCH_7X
        } else {
            SettingPalette.CANCEL_5X
        }
    }

    private val toggleButton by IconButton(
        toggleIcon,
        tooltipText = "".state(),
        enabled = true.state(),
        buttonText = "".state(),
        iconShadow = true.state(),
        textShadow = true.state()
    ).constrain {
        x = 0.pixels(alignOpposite = true)
        width = AspectConstraint()
        height = 100.percent
    }.onLeftClick {
        collapsed.set { !it }
        if (collapsed.get()) {
            textContent.set("")
        } else {
            activateSearch()
        }
    } childOf this

    private val searchContainer by UIBlock(SettingPalette.button).constrain {
        width = expandedWidth.pixels
        height = 100.percent
    }.bindParent(this, !collapsed)

    private val searchIcon by ShadowIcon(SettingPalette.SEARCH_7X, true).constrain {
        x = 5.pixels
        y = CenterConstraint()
    }.rebindPrimaryColor(SettingPalette.textHighlight) childOf searchContainer

    private val searchInput: UITextInput by UITextInput(placeholder = placeholder).constrain {
        x = SiblingConstraint(5f)
        y = CenterConstraint()
        width = FillConstraint(useSiblings = false)
        height = 9.pixels
    } childOf searchContainer

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = 17.pixels
        }

        searchContainer.onLeftClick {
            activateSearch()
        }

        searchInput.onUpdate {
            textContent.set(it)
        }
        textContent.onSetValue {
            if (it != searchInput.getText()) {
                searchInput.setText(it)
            }
        }
    }

    override fun afterInitialization() {
        super.afterInitialization()

        Window.of(this).onKeyType { typedChar, keyCode ->
            when {
                activateOnSearchHokey && keyCode == UKeyboard.KEY_F && UKeyboard.isCtrlKeyDown()
                        && !UKeyboard.isShiftKeyDown() && !UKeyboard.isAltKeyDown() -> {
                    collapsed.set(false)
                    activateSearch()
                }
                activateOnType && !typedChar.isISOControl() -> {
                    collapsed.set(false)
                    activateSearch()
                    searchInput.keyType(typedChar, keyCode)
                }
            }
        }
    }

    fun setText(text: String) {
        searchInput.setText(text)
        textContent.set(text)
    }

    fun getText(): String {
        return textContent.get()
    }

    fun activateSearch() {
        searchInput.grabWindowFocus()
        searchInput.focus()
    }

    fun deactivateSearch() {
        searchInput.releaseWindowFocus()
    }
}
