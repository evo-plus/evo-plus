package ru.dargen.evoplus.features.misc.notify

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.hoverColor
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.scheduler.schedule
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import java.util.concurrent.TimeUnit

object NotifyWidget : WidgetBase {

    // temp, but good
    // TODO: make notify source position based on widget align on screen (check fix!)
    override val node = NotifyListNode().apply {
        align = Relative.RightTop
        origin = Relative.RightTop

        childrenRelative = 1.0

        fixChildSize = true

        indent = v3(8.0, 8.0)

        +hbox {
            color = Colors.TransparentBlack

            indent = v3(7.0, 7.0)
            translation = v3(-16.0)

            +text("Показательное уведомление", "Для настройки виджета") { scale = scale(1.1, 1.1) }

//            this@apply.preTransform { matrices, tickDelta ->
//                if (this@hbox in this@apply.nonComposingChildren) {
//                    if (isWidgetEditor && this@apply.children.size == 1) {
//                        this@apply.nonComposingChildren.remove(this@hbox)
//                        this@hbox.enabled = true
//                        modifications++
//                    }
//                } else if (!isWidgetEditor || this@apply.children.size > 1) {
//                    this@apply.ignore(this@hbox)
//                    this@hbox.enabled = false
//                    modifications++
//                }
//            }

            this@apply.preTransform { _, _ ->
                val isEditor = isWidgetEditor && this@apply.children.size == 1
                val isVisible = this@hbox in this@apply.nonComposingChildren

                if (isEditor && isVisible) {
                    this@apply.nonComposingChildren.remove(this@hbox)
                    this@hbox.enabled = true
                    modifications++
                } else if (!isEditor && !isVisible) {
                    this@apply.ignore(this@hbox)
                    this@hbox.enabled = false
                    modifications++
                }
            }

        }
    }

    fun showText(vararg text: String, delay: Double = 5.0, block: Node.() -> Unit = {}, action: () -> Unit = {}) =
        show(delay) {
            +text(*text) { scale = v3(1.1, 1.1, 1.1) }
            leftClick { _, state ->
                if (isHovered && state && CurrentScreen != null) {
                    action()
                    true
                } else false
            }
            block()
        }

    fun show(delay: Double = 5.0, block: Node.() -> Unit = {}) = node + hbox {
        color = Colors.TransparentBlack
        hoverColor = Colors.TransparentWhite

        fixChildSize = true

        indent = v3(7.0, 7.0)

//        translation = v3(x = -200 + node.parent!!.origin.x * 400.0)
        val originX = node.parent?.origin?.x ?: 1.0
        translation = v3(x = -200.0 + originX * 400.0)

        block()

        var willHide = false

        fun hide() {
            if (isHovered) {
                willHide = true
            } else {
                val hideOriginX = node.parent?.origin?.x ?: 1.0
                animate("state", .8, Easings.BackIn) {
//                translation = v3(x = -200 + this@NotifyWidget.node.parent!!.origin.x * 400.0)
                    translation = v3(x = -200 + hideOriginX * 400.0)
                    after { this@NotifyWidget.node - this@hbox }
                }
            }
        }

        click { _, _, state ->
            if (!isWidgetEditor && CurrentScreen !is GenericContainerScreen && isHovered && state) {
                hide()
                true
            } else false
        }

        hoverOut { if (willHide) hide() }

        recompose()

        animate("state", .8, Easings.BackOut) {
            scale = v3(1.0, 1.0, 1.0)
            translation = v3(-16.0)
        }

        schedule((delay * 1000).toInt(), TimeUnit.MILLISECONDS) { hide() }
    }

    override fun Node.prepare() {
        align = Relative.RightTop
        origin = Relative.RightTop
    }

}