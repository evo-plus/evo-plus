package ru.dargen.evoplus.features.fishing

import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ParticleEvent
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.minusAssign
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import kotlin.collections.contains
import kotlin.time.Duration.Companion.seconds

object FishingSpotsHighlight {

    private val spots = newCacheExpireAfterAccess<Long, CubeOutlineNode>(1.seconds) { _, node -> node.hide() }
    private val SpotTypes =
        listOf(ParticleTypes.BUBBLE, ParticleTypes.BUBBLE_POP, ParticleTypes.BUBBLE_COLUMN_UP, ParticleTypes.LAVA)

    init {
        on<ParticleEvent> {
            if (FishingFeature.SpotsHighlight
                && PlayerDataCollector.location.isFish
                && packet.parameters.type in SpotTypes
            ) with(packet) {
                spots.asMap().getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
                    WorldContext + spotHighlight(x, y, z, offsetX.toDouble()).apply { show() }
                }
            }
        }
    }

    private fun spotHighlight(x: Double, y: Double, z: Double, size: Double) = cubeOutline {
        position = v3(x, y + .5, z)
        this.size = v3(size * 80, size * 80, size * 80)
        origin = v3(.5, .5, .5)
        isSeeThrough = true

        color = Colors.Red
        scale = v3()
    }

    private fun CubeOutlineNode.show() = animate("fade", .2, Easings.BackOut) {
        color = Colors.Green
        scale = v3(1.0, 1.0, 1.0)
    }

    private fun CubeOutlineNode.hide() = animate("fade", .2) {
        color = Colors.Red
        scale = v3()
        after { WorldContext -= this@hide }
    }

}