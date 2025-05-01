package ru.dargen.evoplus.feature.widget

import dev.evoplus.feature.setting.property.attr.WidgetPropertyAttr
import dev.evoplus.feature.setting.property.value.WidgetData
import ru.dargen.evoplus.feature.FeaturesSettings
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.Tips
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.animation.animations
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.ScreenContext
import ru.dargen.evoplus.render.context.screen
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.render.alpha
import kotlin.math.PI

val isWidgetEditor get() = ScreenContext.current()?.id == "features-widgets"

object WidgetEditorScreen {

    var selectedWidget: Widget? = null
    var mode = Mode.MOVE

    fun open() = screen("features-widgets") {
        transparent = true

        selectedWidget = null
        mode = Mode.MOVE

        +rectangle {
            color = Colors.TransparentBlack
            size = v3(25.0, 25.0)
            align = Relative.Center
            origin = Relative.Center

            +text("↩") {
                scale = v3(3.0, 3.0, 1.0)
                align = Relative.Center
                origin = Relative.Center
            }

            var waveOut = true
            tick {
                if ("wave" !in animations) animate("wave", .5, if (waveOut) Easings.BackIn else Easings.BackOut) {
                    scale = if (waveOut) v3(1.5, 1.5, 1.5) else v3(1.0, 1.0, 1.0)
                    rotation = if (isHovered) v3() else v3(z = rotation.z + PI * 2)
                    waveOut = !waveOut
                }
            }

            postRender { mat, _ ->
                if (isHovered) Tips.draw(
                    mat, "Для удаления, переместите виджет область по центру",
                    "Для добавления, нажмите на область по центру"
                )
            }

            hover { _, state ->
                if (state) {
                    if (selectedWidget != null) deleter(this)
                    else animate("hover", .1) {
                        color = Colors.TransparentWhite
                    }
                } else animate("hover", .1) {
                    color = Colors.TransparentBlack
                }
            }
            click { _, _, state ->
                if (state && isHovered) {
                    selector(this)
                    true
                } else false
            }
        }
        destroy { FeaturesSettings.open(1) }
    }.open()

    private fun ScreenContext.deleter(base: Node) = +rectangle {
        color = Colors.TransparentBlack
        size = v3(100.0, 100.0)
        align = Relative.Center
        origin = Relative.Center
        scale = v3(1 / 4.0, 1 / 4.0, 1.0)

        +text("Удалить") {
            scale = v3(2.0, 2.0, 1.0)
            align = Relative.Center
            origin = Relative.Center
        }

        mode = Mode.DELETE
        this@deleter.removeChildren(base)

        fun show() = animate("fade", .1) {
            color = Colors.Red.alpha(63)
            scale = v3(1.0, 1.0, 1.0)
        }

        fun hide() = animate("fade", .1) {
            color = Colors.TransparentBlack
            scale = v3(1 / 4.0, 1 / 4.0, 1 / 4.0)
            after {
                mode = Mode.MOVE
                this@deleter.addChildren(base)
                this@deleter.removeChildren(this@rectangle)
            }
        }

        show()

        hover { _, state -> if (state) show() else hide() }
        tick { if (selectedWidget == null && "fade" !in animations) hide() }
    }

    private fun ScreenContext.selector(base: Node) = +vScrollView {
        color = Colors.TransparentBlack
        align = Relative.Center
        origin = Relative.Center
        resize { size = v3(200.0, Overlay.size.y * .5) }
        scale = v3(1 / 8.0, 1 / 8.0, 1.0)

        fun show() = animate("fade", .1) {
            scale = v3(1.0, 1.0, 1.0)
        }

        fun hide() = animate("fade", .1) {
            scale = v3(1 / 8.0, 1 / 8.0, 1.0)
            after {
                mode = Mode.MOVE
                this@selector.addChildren(base)
                this@selector.removeChildren(this@vScrollView)
            }
        }

        mode = Mode.CREATE
        this@selector.removeChildren(base)

        FeaturesSettings.totalProperties
            .filter { it.attr is WidgetPropertyAttr && it.value.getValue() is WidgetData }
            .map { (it.attr as WidgetPropertyAttr).widget as Widget }
            .filter { it.node.enabled }
            .forEach { widget ->
                addElements(button(widget.name) {
                    on {
                        with(widget) {
                            enabled = true
                            usePosition()
                            with(node) {
                                position = it
                                origin = Relative.Center
                                mouseClick(it, 0, true)
                            }
                        }
                        hide()
                    }
                })
            }

        show()

        hover { _, state -> if (state) show() else hide() }
//        tick { if (selectedWidget == null && "fade" !in animations) hide() }
    }

    enum class Mode {
        CREATE, DELETE, MOVE
    }

}