package ru.dargen.evoplus.feature.screen

import net.minecraft.client.util.InputUtil
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.feature.widget.WidgetEditorScreen
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.Overlay.ScaledResolution
import ru.dargen.evoplus.render.context.ScreenContext
import ru.dargen.evoplus.render.hoverColor
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.VBoxNode
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.input.InputNode
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.render.node.input.input
import ru.dargen.evoplus.render.node.scroll.VScrollViewNode
import ru.dargen.evoplus.render.node.scroll.vScrollView
import ru.dargen.evoplus.resource.Social
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.v3
import kotlin.time.Duration.Companion.seconds

class FeatureScreen : ScreenContext("features", "") {

    companion object {
        private var SelectedFeature = Features.Features.first()
    }

    val content = +rectangle {
        color = Colors.TransparentBlack
        align = Relative.Center
        origin = Relative.Center
        resize { size = this@FeatureScreen.size }
    }

    val label = content + text("${EvoPlus.Label} §fv${EvoPlus.Version}") {
        scale = v3(1.7, 1.7, 1.7)
        position = v3(5.0, 5.0)

        origin = Relative.LeftTop
        align = Relative.LeftTop
    }

    val selectionLabel = content + text(SelectedFeature.name) {
        position = v3(-5.0, 5.0)
        scale = v3(1.7, 1.7, 1.7)

        origin = Relative.RightTop
        align = Relative.RightTop
    }

    lateinit var search: InputNode
    lateinit var selector: VScrollViewNode
    lateinit var selectorBox: VBoxNode
    lateinit var settingsBox: Node

    var prompt = FeaturePrompt()

    val box = content + hbox box@{
        align = Relative.Center
        origin = Relative.Center

        scale = v3()

        space = 1.0

        selectorBox = +vbox {
            indent = v3()
            space = 1.0

            fun switchSetting(feature: Feature = SelectedFeature) {
                SelectedFeature = feature
                settingsBox._childrens.clear()
                settingsBox + SelectedFeature.createElement(prompt)
                selectionLabel.text = feature.name
            }

            fun VScrollViewNode.update() {
                box._childrens = Features.Features
                    .filter { it.search(prompt) }
                    .map {
                        hbox {
                            dependSizeX = false
                            size = v3(130.0, 20.0)
                            +item(it.icon)
                            +text(it.name)
                            preRender { _, _ ->
                                color = if (SelectedFeature != it) Colors.Primary else Colors.Primary.darker()
                            }
                            leftClick { _, state ->
                                if (state && isHovered && it != SelectedFeature) {
                                    switchSetting(it)
                                    true
                                } else false
                            }

                            recompose()
                        }
                    }.toMutableList()
            }

            +hbox {
                indent = v3()
                space = 1.0
                search = +input {
                    size = v3(129.0, 20.0)
                    color = Colors.TransparentBlack
                    prompt.text = "Поиск"

                    on {
                        this@FeatureScreen.prompt = FeaturePrompt(content.takeUnless(String::isBlank))
                        if (!SelectedFeature.search(this@FeatureScreen.prompt)) {
                            Features.Features.firstOrNull { it.search(this@FeatureScreen.prompt) }
                                ?.let { switchSetting(it) }
                        }
                        selector.update()
                        switchSetting()
                    }
                }
                +rectangle {
                    size = v3(20.0, 20.0)
                    color = Colors.TransparentBlack
                    hoverColor = Colors.TransparentWhite
                    +text("X") {
                        align = Relative.Center
                        origin = Relative.Center
                    }
                    leftClick { _, state ->
                        if (state && isHovered) {
                            search.clear()
                            true
                        } else false
                    }
                }
            }
            selector = +vScrollView {
                box.color = Colors.TransparentBlack
                size = v3(150.0)

                update()
                resize { size = v3(size.x, (parent?.size?.y ?: .0) - 21) }
            }
        }

        settingsBox = +delegate { +SelectedFeature.createElement(prompt) }

        resize {
            selectorBox.size = v3(
                selectorBox.size.x,
                ScaledResolution.y * .7,
                .0
            ) //.plus(selector.size.x, .0, .0)// v3(ScaledResolution.x * .7, 35.0, .0)
            settingsBox.size = ScaledResolution.times(.6, .7, .0).minus(selectorBox.size.x, .0, .0)
        }
    }

    init {
        content + button {
            align = Relative.RightBottom
            origin = Relative.RightBottom

            translation = v3(-3.0, -3.0)
            size = v3(70.0, 20.0)

            this@button.label.text = "Виджеты"

            on { WidgetEditorScreen.open() }
        }

        content + hbox {
            space = 4.0
            indent = v3(4.0, 4.0)

            align = Relative.LeftBottom
            origin = Relative.LeftBottom


            Social.entries.forEach { social -> +socialIcon(social) }
        }

        display {
            box.animate("show", .2, Easings.BackOut) {
                box.scale = v3(1.0, 1.0, 1.0)
            }
        }
//        destroy { async(Features::saveSettings) }

        typeKey(InputUtil.GLFW_KEY_F) {
            if (net.minecraft.client.gui.screen.Screen.hasControlDown()) {
                search.safeCast<InputNode>()?.focused = true
                true
            } else false
        }
    }


    fun socialIcon(social: Social) = rectangle {
        size = v3(32.0, 32.0)

        +texture(social.identifier) {
            align = Relative.Center
            origin = Relative.Center
            size = v3(32.0, 32.0)
            textureSize = v3(32.0, 32.0)
            hover { _, state ->
                animate("hover", 0.15.seconds, Easings.CubicOut) {
                    scale = if (state) v3(.8, .8, .8) else v3(1.0, 1.0, 1.0)
                }
            }
            leftClick { _, state ->
                if (state && isHovered) {
                    social.open()
                    true
                } else false
            }
        }
    }

}