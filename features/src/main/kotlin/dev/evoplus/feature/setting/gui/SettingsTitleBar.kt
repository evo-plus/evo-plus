package dev.evoplus.feature.setting.gui

import dev.evoplus.feature.setting.Settings
import dev.evoplus.feature.setting.gui.common.IconButton
import dev.evoplus.feature.setting.utils.onLeftClick
import dev.evoplus.feature.setting.utils.state
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*

class SettingsTitleBar(private val gui: SettingsGui, private val settings: Settings, window: Window) :
    UIContainer() {

    // Notches in titlebar
    private val leftDivider by UIBlock(SettingPalette.componentHighlight).constrain {
        width = SettingsGui.dividerWidth.pixels
        height = 100.percent
    } childOf this

    private val contentContainer by UIBlock(SettingPalette.getComponentBackground()).constrain {
        x = SiblingConstraint()
        width = 100.percent - (SettingsGui.dividerWidth * 2f).pixels
        height = 100.percent
    } childOf this

    private val rightDivider by UIBlock(SettingPalette.componentHighlight).constrain {
        x = 0.pixels(alignOpposite = true)
        width = SettingsGui.dividerWidth.pixels
        height = 100.percent
    } childOf this

    private val titleText by UIText(settings.localizedName).constrain {
        x = 10.pixels
        y = CenterConstraint()
    } childOf contentContainer

    private val middleDivider by UIBlock(SettingPalette.componentHighlight).constrain {
        x = 25.percent + SettingsGui.dividerWidth.pixels
        width = SettingsGui.dividerWidth.pixels
        height = 100.percent
    } childOf this

    private val searchBar by Searchbar().constrain {
        x = 25.percent + SettingsGui.dividerWidth.pixels + 13.pixel
        y = CenterConstraint()
        height = 17.pixels
    } childOf this

    private val subscriptionOnly by IconButton(
        SettingPalette.SUBSCRIPTION.state(),
        tooltipText = "Функционал от подписки".state(),
        enabled = settings.guiPreferences.subscription.state(),
        buttonText = "".state(),
        iconShadow = true.state(),
        textShadow = true.state()
    ).apply {
        icon.constrain {
            width = 10.pixels
            height = 10.pixels
        }
    }.constrain {
        x = 13.pixels(alignOpposite = true)
        y = CenterConstraint()
        width = 17.pixels
        height = 17.pixels
    } childOf this

    init {
        constrain {
            width = 100.percent
            height = 30.pixels
        }

        searchBar.textContent.onSetValue {
            gui.selectCategory(settings.searchProperties(it))
        }

        subscriptionOnly.onLeftClick {
            this as IconButton
            settings.guiPreferences.subscription = !settings.guiPreferences.subscription
            rebindEnabled(settings.guiPreferences.subscription.state())
            gui.update()
        }

    }

}
