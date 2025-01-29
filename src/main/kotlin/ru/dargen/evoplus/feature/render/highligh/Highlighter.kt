package ru.dargen.evoplus.feature.render.highligh

import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import java.awt.Color
import kotlin.time.Duration

@KotlinOpens
class Highlighter(val expire: Duration) {

    protected val color: Color = Colors.Green

    private val cache = newCacheExpireAfterAccess<Long, CubeOutlineNode>(expire) { _, node -> node.hide() }

    protected fun createHighlight(
        x: Double, y: Double, z: Double,
        size: Double = 1.0, color: Color = this.color,
    ) = cache.asMap().getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
        WorldContext + highlight(x, y, z, size, color).apply { show() }
    }

    protected fun highlight(
        x: Double, y: Double, z: Double,
        size: Double = 1.0, color: Color = this.color,
    ) = cubeOutline {
        position = v3(x, y, z)
        scaledSize = v3(size, size, size)
        origin = v3(.5, .5, .5)
        isSeeThrough = true
        width = 3.0

        this.color = color
        scale = v3()
    }

    protected fun CubeOutlineNode.show() = animate("fade", .2, Easings.BackOut) {
        scale = v3(1.0, 1.0, 1.0)
    }

    protected fun CubeOutlineNode.hide() = animate("fade", .2) {
        scale = v3()
        after { WorldContext + this@hide }
    }

}