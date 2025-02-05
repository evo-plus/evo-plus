package ru.dargen.evoplus.feature.render.highligh

import net.minecraft.particle.ParticleType
import org.joml.Vector3f
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ParticleEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@KotlinOpens
abstract class ParticleHighlighter(val enabled: () -> Boolean, expire: Duration = 1.seconds) : Highlighter(expire) {

    protected val particles: List<ParticleType<*>> = emptyList()

    init {
        on<ParticleEvent> {
            if (enabled() && shouldProcess() && packet.parameters.type in particles) with(packet) {
                createHighlight(x, y, z, Vector3f(offsetX * 2f))
            }
        }
    }

    protected fun shouldProcess() = true

}