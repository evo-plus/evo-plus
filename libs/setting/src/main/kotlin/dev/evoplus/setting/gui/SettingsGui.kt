package dev.evoplus.setting.gui

import dev.evoplus.setting.Settings
import dev.evoplus.setting.gui.common.IconButton
import dev.evoplus.setting.gui.settings.*
import dev.evoplus.setting.property.data.CategoryData
import dev.evoplus.setting.utils.onLeftClick
import dev.evoplus.setting.utils.scrollGradient
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.AspectConstraint
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.GuiScale
import gg.essential.universal.UKeyboard

class SettingsGui(private val config: Settings) : WindowScreen(
    version = ElementaVersion.V2,
    newGuiScale = GuiScale.scaleForScreenSize().ordinal,
    restoreCurrentGuiOnClose = true,
) {

    private val background by UIBlock(SettingPalette.mainBackground).constrain {
        width = 100.percent
        height = 100.percent
    } childOf window

    private val container by UIBlock(SettingPalette.mainBackground.map { it.withAlpha(1f) }).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 85.percent
        height = 75.percent
    } childOf window

    private val titleBar by SettingsTitleBar(this, config, window) childOf container

    private val bottomContainer by UIContainer().constrain {
        y = SiblingConstraint()
        width = 100.percent
        height = FillConstraint()
    } childOf container

    private val leftDivider by UIBlock(SettingPalette.dividerDark).constrain {
        width = dividerWidth.pixels
        height = 100.percent
    } childOf bottomContainer

    private val sidebar by UIContainer().constrain {
        x = SiblingConstraint()
        width = 25.percent
        height = 100.percent
    } effect ScissorEffect() childOf bottomContainer

    private val middleDivider by UIBlock(SettingPalette.dividerDark).constrain {
        x = SiblingConstraint()
        width = dividerWidth.pixels
        height = 100.percent
    } childOf bottomContainer

    private val sidebarScroller by ScrollComponent(
        "Категории не найдены ;(",
        innerPadding = 10f,
        pixelsPerScroll = 25f,
    ).constrain {
        width = 100.percent
        height = 100.percent - dividerWidth.pixels
    } childOf sidebar scrollGradient 20.pixels

    private val sidebarVerticalScrollbar by UIBlock(SettingPalette.scrollbar).constrain {
        width = 100.percent
    } childOf middleDivider

    private val rightDivider by UIBlock(SettingPalette.dividerDark).constrain {
        x = 0.pixels(alignOpposite = true)
        width = dividerWidth.pixels
        height = 100.percent
    } childOf bottomContainer

    private val content by UIContainer().constrain {
        x = SiblingConstraint() boundTo middleDivider
        width = FillConstraint(useSiblings = false)
        height = 100.percent
    } effect (ScissorEffect()) childOf bottomContainer

    private val bottomDivider by UIBlock(SettingPalette.dividerDark).constrain {
        y = SiblingConstraint()
        width = 100.percent
        height = dividerWidth.pixels
    } childOf container

    private val sidebarHorizontalScrollbarContainer by UIContainer().constrain {
        x = 0.pixels boundTo sidebar
        width = 100.percent boundTo sidebar
        height = dividerWidth.pixels
    } childOf bottomDivider

    private val sidebarHorizontalScrollbar by UIBlock(SettingPalette.scrollbar).constrain {
        height = 100.percent
    } childOf sidebarHorizontalScrollbarContainer

    private val backButton by IconButton(SettingPalette.ARROW_LEFT_4X7).constrain {
        x = SiblingConstraint(18f, alignOpposite = true) boundTo titleBar
        y = CenterConstraint() boundTo titleBar
        width = 17.pixels
        height = AspectConstraint()
    } childOf window

    private lateinit var categories: Map<String, CategoryData>
    private lateinit var currentCategory: SettingsCategory

    init {
        backButton.onActiveClick { restorePreviousScreen() }

        window.onLeftClick { currentCategory.closePopups() }

        sidebarScroller.setVerticalScrollBarComponent(sidebarVerticalScrollbar, true)
        sidebarScroller.setHorizontalScrollBarComponent(sidebarHorizontalScrollbar, true)

        update()

        fun UIComponent.click(): Unit =
            mouseClick(getLeft() + (getRight() - getLeft()) / 2.0, getTop() + (getBottom() - getTop()) / 2.0, 0)

        window.onKeyType { _, keyCode ->
            if (UKeyboard.isShiftKeyDown() && keyCode == UKeyboard.KEY_MINUS) {
                Inspector(window) childOf window
                return@onKeyType
            }

            if (keyCode == UKeyboard.KEY_ESCAPE) {
                restorePreviousScreen()
                return@onKeyType
            }

            currentCategory.scroller.childrenOfType<DataBackedSetting>().filter { it.isHovered() }.forEach { child ->
                when (child.component) {
                    is AbstractSliderComponent -> if (keyCode == UKeyboard.KEY_LEFT) {
                        child.component.incrementBy(-.05f)
                    } else if (keyCode == UKeyboard.KEY_RIGHT) {
                        child.component.incrementBy(.05f)
                    }

                    is NumberComponent -> if (keyCode == UKeyboard.KEY_UP) {
                        child.component.increment()
                    } else if (keyCode == UKeyboard.KEY_DOWN) {
                        child.component.decrement()
                    }

                    is SwitchComponent -> when (keyCode) {
                        UKeyboard.KEY_LEFT -> if (child.component.enabled.get()) child.component.click()
                        UKeyboard.KEY_RIGHT -> if (!child.component.enabled.get()) child.component.click()
                        UKeyboard.KEY_ENTER -> child.component.click()
                    }

                    is CheckboxComponent -> if (keyCode == UKeyboard.KEY_ENTER) child.component.click()
                    is ButtonComponent -> if (keyCode == UKeyboard.KEY_ENTER) child.component.click()
                    is SelectorComponent -> if (keyCode == UKeyboard.KEY_UP) {
                        child.component.dropDown.select(child.component.dropDown.selectedIndex.get() - 1)
                    } else if (keyCode == UKeyboard.KEY_DOWN) {
                        child.component.dropDown.select(child.component.dropDown.selectedIndex.get() + 1)
                    }
                }
                return@forEach
            }
        }
    }

    fun update() {
        if (::currentCategory.isInitialized) {
            content.removeChild(currentCategory)
        }

        categories = config.getCategoriesData()
        currentCategory = SettingsCategory(categories.getOrElse(config.guiPreferences.selected) {
            categories.values.firstOrNull() ?: CategoryData.Empty
        }) childOf content

        sidebarScroller.clearChildren()
        categories.values.forEach { category ->
            val label = CategoryLabel(this, category)
            label childOf sidebarScroller
            if (currentCategory.category === category) {
                label.select()
            }
        }
    }

    fun selectCategory(category: CategoryData) {
        val newCategory = SettingsCategory(category) childOf content

        currentCategory.closePopups(true)
        currentCategory.hide()
        newCategory.unhide()
        newCategory.scrollToTop()
        currentCategory = newCategory
        categories.filter { it.value === category }
            .keys.firstOrNull()
            ?.let { config.guiPreferences.selected = it }
        sidebarScroller.childrenOfType<CategoryLabel>().firstOrNull { it.isSelected }?.deselect()
    }

    override fun updateGuiScale() {
        newGuiScale = GuiScale.scaleForScreenSize().ordinal
        super.updateGuiScale()
    }

    companion object {
        internal const val dividerWidth = 3f
    }
}
