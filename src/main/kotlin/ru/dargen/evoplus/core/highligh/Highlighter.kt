package ru.dargen.evoplus.core.highligh

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.WorldRenderEvent
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.smooth
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import ru.dargen.evoplus.util.render.shape.Box
import java.awt.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@KotlinOpens
class Highlighter(val expire: Duration) {

    protected val color: Color = Colors.Green

    private val cache = newCacheExpireAfterAccess<Long, Box>(expire)

    init {
        on<WorldRenderEvent.Absolute> {
            cache.asMap().values.forEach { it.draw() }
        }
    }

    protected fun createHighlight(
        x: Double, y: Double, z: Double,
        size: Vector3f = Vector3f(0f), color: Color = this.color,
    ) = cache.asMap().getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
        highlight(x, y, z, size, color)
    }

    protected fun highlight(
        x: Double, y: Double, z: Double,
        size: Vector3f, color: Color = this.color,
    ): Box {
        val boxObj = net.minecraft.util.math.Box.of(Vec3d(x, y, z), size.x.toDouble(), size.y.toDouble(), size.z.toDouble())
        val box = Box(boxObj, color)

        box.smooth("show", .2.seconds, Easings.BackOut) {
            box::box.to(size)
            box::color.to(color)
        }
        return box
    }

}