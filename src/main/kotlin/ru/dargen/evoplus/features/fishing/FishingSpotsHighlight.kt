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

    override fun highlight(x: Double, y: Double, z: Double, size: Vector3f, color: Color): Box {
        return super.highlight(x, y + .8, z, Vector3f(size.x * 2.4f, size.y / 2f, size.z * 2.4f), color)
    }

    override fun shouldProcess() = PlayerDataCollector.location.warp == "fish"

}