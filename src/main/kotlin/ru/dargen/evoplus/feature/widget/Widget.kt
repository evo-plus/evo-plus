package ru.dargen.evoplus.feature.widget

import com.google.gson.JsonElement
import ru.dargen.evoplus.feature.setting.Setting
import ru.dargen.evoplus.render.Colors.Transparent
import ru.dargen.evoplus.render.Colors.TransparentWhite
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.Tips
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.box
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.json.Gson
import ru.dargen.evoplus.util.json.asDouble
import ru.dargen.evoplus.util.json.asObject
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class Widget(id: String, name: String, supplier: Node.() -> Unit) : Setting<Node>(id, name) {

    var position = false
    var enabled
        get() = value.enabled
        set(enabled) {
            value.enabled = enabled
        }

    private var hoverTimestamp = 0L

    override var value: Node = Overlay + box {
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
                        this@Widget.enabled = false
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
        hover {  _, state -> if (isWidgetEditor) hoverTimestamp = if (state) currentMillis else 0L }
        preTransform { _, _ -> color = if (isWidgetEditor && isHovered) TransparentWhite else Transparent }
        postRender { context, _ ->
            if (isWidgetEditor && isHovered && hoverTimestamp + 1000 <= currentMillis) Tips.draw(
                context, "Для изменения размера используйте колесико мышки.",
                "Чтобы вернуть размер по умолчанию, нажмите на колесико мышки."
            )
        }
    }

    private fun fix() {
        usePosition()
        fixPosition()
        useAlign()
    }

    private fun fixPosition() = value.apply {

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

    fun usePosition() = value.apply {
        if (this@Widget.position) return@apply
        this@Widget.position = true

        position = parent!!.size * align
        align = v3()
    }

    private fun useAlign() = value.apply {
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

    override fun load(element: JsonElement) {
        val element = element.asObject() ?: return

        enabled = element["enabled"]?.asBoolean ?: enabled
        element["origin"].asObject()?.let {
            value.origin.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
        element["align"].asObject()?.let {
            value.align.apply {
                set(
                    it["x"].asDouble(x.coerceAtLeast(.0)),
                    it["y"].asDouble(y).coerceAtLeast(.0),
                    it["z"].asDouble(z).coerceAtLeast(.0)
                )
            }
        }
        element["scale"].asObject()?.let {
            value.scale.apply { set(it["x"].asDouble(x), it["y"].asDouble(y), it["z"].asDouble(z)) }
        }
    }

    override fun store(): JsonElement = Gson.toJsonTree(
        mapOf(
            "enabled" to enabled,
            "align" to value.align.toMap(),
            "scale" to value.scale.toMap(),
            "origin" to value.origin.toMap()
        )
    )

}