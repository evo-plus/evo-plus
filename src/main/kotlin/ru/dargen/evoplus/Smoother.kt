package ru.dargen.evoplus

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.dargen.evoplus.render.animation.Easing
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.target.AnimationTargetType
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import kotlin.concurrent.thread
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.javaType
import kotlin.time.Duration

object Smoother {

    val contexts = concurrentHashMapOf<Any, MutableMap<String, SmoothContext>>()

    init {
        thread(start = true, isDaemon = true, name = "Smoother") {
            while (true) {
                val now = Clock.System.now()
                contexts.forEach { (key, value) ->
                    value.entries.removeIf { (_, context) ->
                        if (now < context.timestamp + context.duration && context.predicate()) {
                            val progress =
                                (now - context.timestamp).inWholeMilliseconds / context.duration.inWholeMilliseconds.toDouble()
                            context.lerp(progress)
                            false
                        } else true
                    }
                    if (value.isEmpty()) contexts.remove(key)
                }
                Thread.sleep(20)
            }
        }
    }


}

inline fun <T> T.smooth(
    name: String,
    duration: Duration,
    noinline easing: Easing = Easings.Linear,
    block: SmoothModifier.() -> Unit,
): SmoothContext {
    val modifier = SmoothModifier(name, Clock.System.now(), duration, easing)
    modifier.block()
    val context = SmoothContext(
        name,
        modifier.timestamp, modifier.duration, modifier.easing,
        modifier.properties, modifier.predicate
    )
    Smoother.contexts.getOrPut(this as Any) { mutableMapOf() }[name] = context
    return context
}

class SmoothModifier(
    val name: String,
    val timestamp: Instant, val duration: Duration, val easing: Easing,
    var predicate: () -> Boolean = { true },
) {

    val properties = mutableSetOf<SmoothProperty<*>>()

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified T : Any> KMutableProperty0<T>.to(
        value: T,
        type: AnimationTargetType<T> = AnimationTargetType.forType<T>(returnType.javaType),
    ) = properties.add(SmoothProperty(this, SmoothValue(get(), value, type)))

}

data class SmoothContext(
    val name: String,
    val timestamp: Instant, val duration: Duration, val easing: Easing,
    private val properties: Set<SmoothProperty<*>>, val predicate: () -> Boolean,
) {

    fun lerp(progress: Double) {
        val progress = easing(progress)
        properties.forEach { it.lerp(progress) }
    }

}

data class SmoothProperty<T>(val property: KMutableProperty0<T>, val value: SmoothValue<T>) {

    fun lerp(progress: Double) = property.set(value.at(progress))

}

data class SmoothValue<T>(val prev: T, val next: T, val type: AnimationTargetType<T>) {

    fun at(progress: Double) = type.progressTo(prev, next, progress)

}