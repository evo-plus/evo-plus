package ru.dargen.evoplus.feature.render.highligh

import net.minecraft.util.math.BlockPos
import org.joml.Vector3f
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.WorldRenderEvent
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import ru.dargen.evoplus.util.render.shape.Box
import java.awt.Color
import kotlin.time.Duration

@KotlinOpens
class Highlighter(val expire: Duration) {

    protected val color: Color = Colors.Green

    private val cache = newCacheExpireAfterAccess<Long, Box>(expire)

    init {
        on<WorldRenderEvent.Absolute> {
            cache.asMap().values.forEach { it.draw(matrices) }
        }
    }

    protected fun createHighlight(
        x: Double, y: Double, z: Double,
        size: Double = 1.0, color: Color = this.color,
    ) = cache.asMap().getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
        highlight(x, y, z, size, color)
    }

    protected fun highlight(
        x: Double, y: Double, z: Double,
        size: Double = 1.0, color: Color = this.color,
    ) = Box(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()), Vector3f(size.toFloat()), color, true)

}