package ru.dargen.evoplus.features.fishing

import net.minecraft.particle.ParticleTypes
import ru.dargen.evoplus.feature.render.highligh.ParticleHighlighter
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

object FishingSpotsHighlight : ParticleHighlighter(FishingFeature::SpotsHighlight) {

    override val particles = listOf(
        ParticleTypes.BUBBLE, ParticleTypes.BUBBLE_POP,
        ParticleTypes.BUBBLE_COLUMN_UP, ParticleTypes.LAVA
    )

    override fun highlight(x: Double, y: Double, z: Double, size: Double, color: Color): CubeOutlineNode {
        return super.highlight(x, y + .8, z, size, color).apply {
            scaledSize = v3(size * 2.4, size / 2, size * 2.4)
        }
    }

    override fun CubeOutlineNode.hide() = animate("fade", .2) {
        scale = v3()
        color = Colors.Red
        after { WorldContext + this@hide }
    }

    override fun shouldProcess() = PlayerDataCollector.location.warp == "fish"

}