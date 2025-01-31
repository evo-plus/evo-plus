package dev.evoplus.setting.gui

import dev.evoplus.setting.property.data.CategoryData
import dev.evoplus.setting.utils.scrollGradient
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.toConstraint

class SettingsCategory(category: CategoryData) : UIContainer() {

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

        val categoryItemsSettingsObjects: ArrayList<DataBackedSetting> = ArrayList()
        val dividerItemsSettingsObjects: ArrayList<Divider> = ArrayList()

        category.items.forEach {
            val settingsObject = it.toSettingsObject()
            if (settingsObject != null) {
                settingsObject childOf scroller
                if (settingsObject is DataBackedSetting) {
                    categoryItemsSettingsObjects.add(settingsObject)
                    if (settingsObject.data.isHidden()) {
                        settingsObject.hide(true)
                    }
                } else if (settingsObject is Divider) {
                    var flag = false
                    var flag2 = false
                    for (item in category.items) {
//                        if (item is PropertyItem && item.subcategory == settingsObject.name) {
//                            if (!item.data.isHidden()) {
//                                flag2 = true
//                                break
//                            }
//                        }
                    }
                    if (flag) {
                        dividerItemsSettingsObjects.add(settingsObject)
                        if (!flag2) {
                            settingsObject.hide(true)
                            settingsObject.hidden = true
                        }
                    }
                }
            }
        }

        scroller.setVerticalScrollBarComponent(scrollBar, true)

        scroller.onMouseScroll {
            closePopups()
        }
    }

    @JvmOverloads
    fun closePopups(instantly: Boolean = false) {
        scroller.childrenOfType<Setting>().forEach {
            it.closePopups(instantly)
        }
    }

    fun scrollToTop() {
        scroller.scrollToTop(smoothScroll = false)
    }
}
