package ru.dargen.evoplus.feature.widget

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.attr.WidgetPropertyAttr
import dev.evoplus.feature.setting.property.value.WidgetData
import gg.essential.elementa.utils.Vector3f
import ru.dargen.evoplus.render.Colors.Transparent
import ru.dargen.evoplus.render.Colors.TransparentWhite
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.Tips
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.box
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class Widget(override val id: String, val name: String, supplier: Node.() -> Unit) : WidgetPropertyAttr.WidgetAccessor {

    var position = false

    private var hoverTimestamp = 0L

    var node: Node = Overlay + box {
        supplier()

        color = Transparent
        vWheel { _, wheel ->
            if (isWidgetEditor && isHovered) {
                scale = (scale + wheel / 10.0).fixIn(.2, 4.0)
                fix()
                true
            } else false
        }
        click(2) { _, state ->
            if (isWidgetEditor && isHovered && state) {
                animate("scale", .2) { scale = v3(1.0, 1.0, 1.0) }
                true
            } else false
        }
        drag(inOutHandler = {
            if (it && isWidgetEditor) {
                WidgetEditorScreen.selectedWidget = this@Widget
                usePosition()
            } else if (!it && this@Widget.position) {
                if (WidgetEditorScreen.selectedWidget === this@Widget) {
                    if (WidgetEditorScreen.mode === WidgetEditorScreen.Mode.DELETE) {
                        enabled = false
                    }
                    WidgetEditorScreen.selectedWidget = null
                }

                useAlign()
            }
        }) { _, delta ->
            if (isWidgetEditor) {
                translation = delta / (wholeScale / scale)

                fixPosition()
            }
        }
        hover { _, state -> if (isWidgetEditor) hoverTimestamp = if (state) currentMillis else 0L }
        preTransform { _, _ -> color = if (isWidgetEditor && isHovered) TransparentWhite else Transparent }
        postRender { matrices, _ ->
            if (isWidgetEditor && isHovered && hoverTimestamp + 1000 <= currentMillis) Tips.draw(
                matrices, "Для изменения размера используйте колесико мышки.",
                "Чтобы вернуть размер по умолчанию, нажмите на колесико мышки."
            )
        }
    }

    private fun fix() {
        usePosition()
        fixPosition()
        useAlign()
    }

    private fun fixPosition() = node.apply {

        val scale = (wholeScale / scale)
        val minPosition = wholePosition / scale
        val maxPosition = minPosition + wholeSize / scale

        val (minX, minY) = minPosition
        val (maxX, maxY) = maxPosition

        val (parentX, parentY) = parent!!.size

        if (minX < 0) {
            translation.x -= minX
        } else if (maxX > parentX) {
            translation.x -= maxX - parentX
        }

        if (minY < 0) {
            translation.y -= minY
        } else if (maxY > parentY) {
            translation.y -= maxY - parentY
        }
    }

    fun usePosition() = node.apply {
        if (this@Widget.position) return@apply
        this@Widget.position = true

        position = parent!!.size * align
        align = v3()
    }

    private fun useAlign() = node.apply {
        this@Widget.position = false

        var pos = position + translation

        position = v3()
        translation = v3()

        val centeredAlign = ((pos - size * origin * scale + size * scale / 2.0) / parent!!.size).fixNaN()

        val origin = Relative.entries.minBy { it.distance(centeredAlign) }
        pos += (origin - this.origin) * size * scale

        this.origin = origin
        align = (pos / parent!!.size).fixNaN()
    }

    override fun update(data: WidgetData) {
        node.enabled = data.enabled
        node.scale = v3(data.scale)
        node.origin = data.origin.run { Vector3(x.toDouble(), y.toDouble(), z.toDouble()) }
        node.align = data.align.run { Vector3(x.toDouble(), y.toDouble(), z.toDouble()) }
    }

    override fun snapshot(data: WidgetData) {
        data.enabled = node.enabled
        data.scale = node.scale.x
        data.origin = node.origin.run { Vector3f(x.toFloat(), y.toFloat(), z.toFloat()) }
        data.align = node.align.run { Vector3f(x.toFloat(), y.toFloat(), z.toFloat()) }
    }

}

fun CategoryBuilder.widget(widget: Widget, enabled: Boolean = widget.node.enabled) =
    widget(widget.name, id = widget.id, widget = widget, enabled = enabled)

fun CategoryBuilder.widget(id: String, name: String, widget: Node.() -> Unit, enabled: Boolean = true) =
    widget(Widget(id, name, widget), enabled = enabled)