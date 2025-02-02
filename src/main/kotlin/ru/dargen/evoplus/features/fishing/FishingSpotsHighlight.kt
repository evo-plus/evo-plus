package ru.dargen.evoplus.features.fishing

import net.minecraft.particle.ParticleTypes
import org.joml.Vector3f
import ru.dargen.evoplus.feature.render.highligh.ParticleHighlighter
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.util.render.shape.Box
import java.awt.Color

object FishingSpotsHighlight : ParticleHighlighter(FishingFeature::SpotsHighlight) {

    override val particles = listOf(
        ParticleTypes.BUBBLE, ParticleTypes.BUBBLE_POP,
        ParticleTypes.BUBBLE_COLUMN_UP, ParticleTypes.LAVA
    )

    override fun highlight(x: Double, y: Double, z: Double, size: Double, color: Color): Box {
        val size = size.toFloat()
        return super.highlight(x, y + .8, z, size.toDouble(), color).apply {
            this.size = Vector3f(size * 2.4f, size / 2f, size * 2.4f)
        }
    }

    override fun shouldProcess() = PlayerDataCollector.location.warp == "fish"

}