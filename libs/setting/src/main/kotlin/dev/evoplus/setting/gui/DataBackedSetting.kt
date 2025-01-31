package dev.evoplus.setting.gui

import gg.essential.elementa.components.SVGComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.toConstraint
import dev.evoplus.setting.gui.elementa.GuiScaleOffsetConstraint
import dev.evoplus.setting.gui.settings.SettingComponent
import dev.evoplus.setting.property.Property

class DataBackedSetting(
    internal val data: Property<*>,
    internal val component: SettingComponent,
) : Setting() {

    private val boundingBox: UIBlock by UIBlock(SettingPalette.componentBackground.toConstraint()).constrain {
        x = 1.pixels
        y = 1.pixels
        width = 100.percent - 2.pixels
        height = ChildBasedMaxSizeConstraint() + INNER_PADDING.pixels
    } childOf this effect OutlineEffect(
        SettingPalette.getComponentBorder(),
        1f
    ).bindColor(SettingPalette.componentBorder)

    private val textBoundingBox by UIContainer().constrain {
        x = INNER_PADDING.pixels
        y = INNER_PADDING.pixels
        width = basicWidthConstraint { component ->
            val endPos = ((boundingBox.children - component).minOfOrNull { it.getLeft() } ?: boundingBox.getRight())
            (endPos - component.getLeft() - 10f).coerceAtMost(364f)
        }
        height = ChildBasedSizeConstraint(3f) + INNER_PADDING.pixels
    } childOf boundingBox


    init {
        SVGComponent.ofResource("/assets/evo-plus/textures/gui/settings/x.svg").constrain {
//            x = SiblingConstraint(5f)
        } childOf textBoundingBox
        UIWrappedText(
            data.meta.localizedName,
            shadowColor = SettingPalette.getTextShadowLight()
        ).constrain {
            width = 100.percent
            textScale = GuiScaleOffsetConstraint(1f)
            color = SettingPalette.textHighlight.toConstraint()
        } childOf textBoundingBox
        UIWrappedText(
            data.meta.localizedDescription,
            shadowColor = SettingPalette.getTextShadowLight(),
            lineSpacing = 10f
        ).constrain {
            y = SiblingConstraint() + 3.pixels
            width = 100.percent
            color = SettingPalette.text.toConstraint()
        } childOf textBoundingBox
    }

    private var hidden = data.isHidden()

    init {

        constrain {
            y = SiblingConstraint(8f)
            height = ChildBasedMaxSizeConstraint() + 2.pixels
        }

        component.observe {
            data.setValue(it)
        }
        component childOf boundingBox
        component.setupParentListeners(this)
    }

    fun hideMaybe() {
        if (hidden) {
            if (!data.isHidden()) {
                hidden = false
                unhide()
            }
        } else if (data.isHidden()) {
            hidden = true
            hide(true)
        }
    }

    override fun closePopups(instantly: Boolean) {
        component.closePopups(instantly)
    }

    companion object {
        const val INNER_PADDING = 13f
    }
}
