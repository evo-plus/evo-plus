package ru.dargen.evoplus.render.node.input.selector.scroll

import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.node.drag
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.MousePosition

class VScrollSelectorNode<T> : AbstractScrollSelectorNode<T>() {

    override val hook = +button {
        isSilent = true
        size = v3(y = 5.0)
        align = ru.dargen.evoplus.render.Relative.CenterTop
        origin = ru.dargen.evoplus.render.Relative.Center
    }
    override val label = +text {
        align = ru.dargen.evoplus.render.Relative.Center
        origin = ru.dargen.evoplus.render.Relative.Center
    }

    var rotateLabel = false
        set(value) {
            field = value
            label.rotation = if (value)
                v3(z = if (wholePosition.x + wholeSize.x / 2 > Overlay.ScaledResolution.x / 2) 90.0 else -90.0).radians()
            else v3()
        }
    override var size: Vector3
        get() = super.size
        set(value) {
            super.size = value
            hook.size.x = value.x + 1.0
        }

    init {
        rotateLabel = true
        size = Vector3(20.0, 100.0)

        drag { _, _ ->
            val index = (((MousePosition.y - wholePosition.y) / wholeSize.y) * (selector.size - 1)).toInt()
            if (selector.index != index) {
                selector.selectOn(index)
            }
        }
    }

    override fun updateHook() {
        hook.animate("move", .16, Easings.BackOut) {
            hook.position = v3(
                y = hook.size.y / 2
                        + (size.y - hook.size.y)
                        * (selector.index / ((selector.size - 1).coerceAtLeast(1)).toDouble())
            )
        }
    }

}

fun <T> vScrollSelector(block: VScrollSelectorNode<T>.() -> Unit = {}) = VScrollSelectorNode<T>().apply(block)