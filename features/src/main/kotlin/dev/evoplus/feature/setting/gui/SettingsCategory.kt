package dev.evoplus.feature.setting.gui

import dev.evoplus.feature.setting.property.data.CategoryData
import dev.evoplus.feature.setting.utils.scrollGradient
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint

class SettingsCategory(val category: CategoryData) : UIContainer() {

    internal val scroller by ScrollComponent(
        "Настройки не найдены ;(",
        innerPadding = 10f,
        pixelsPerScroll = 25f,
    ).constrain {
        width = 100.percent - (10 + SettingsGui.dividerWidth).pixels
        height = 100.percent
    } childOf this scrollGradient 20.pixels

    private val scrollBar by UIBlock(SettingPalette.scrollbar).constrain {
        x = 0.pixels(alignOpposite = true)
        width = SettingsGui.dividerWidth.pixels
    } childOf this

    init {

        constrain {
            width = 100.percent
            height = 100.percent
        }

        if (category.meta.hasDescription) {
            UIWrappedText(
                category.meta.localizedDescription,
                shadowColor = SettingPalette.getTextShadowLight(),
                centered = true
            ).constrain {
                x = CenterConstraint()
                y = SiblingConstraint(DataBackedSetting.INNER_PADDING)
                width = 100.percent - (DataBackedSetting.INNER_PADDING * 2f).pixels
                color = SettingPalette.text.toConstraint()
            } childOf scroller
        }

        category.items.forEach {
            val settingsObject = it.toSettingsObject()
            if (settingsObject != null) {
                settingsObject childOf scroller
                if (settingsObject is DataBackedSetting) {
                    if (settingsObject.data.meta.hidden) {
                        settingsObject.hide(true)
                    }
                }
            }
        }

        scroller.setVerticalScrollBarComponent(scrollBar, true)

        scroller.onMouseScroll { closePopups() }
    }

    fun closePopups(instantly: Boolean = false) {
        scroller.childrenOfType<Setting>().forEach {
            it.closePopups(instantly)
        }
    }

    fun scrollToTop() {
        scroller.scrollToTop(smoothScroll = false)
    }
}
