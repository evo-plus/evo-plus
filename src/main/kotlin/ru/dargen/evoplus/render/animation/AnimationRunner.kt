package ru.dargen.evoplus.render.animation

import ru.dargen.evoplus.render.node.DummyNode
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.WindowInitialized
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object AnimationRunner {

    val Animations: MutableMap<Node, MutableMap<String, Animation<*>>> = ConcurrentHashMap()

    init {
        thread(isDaemon = true, name = "Animation-Thread") {
            while (true) runCatching/*catch("Error while running animations")*/ {
                run()

                val delay = if (WindowInitialized) Client.currentFps.fix(10, 200).rateDelay else 50
                Thread.sleep(delay)
            }
        }
    }

    @Synchronized
    fun <N : Node?> run(context: AnimationContext<N>, builder: AnimationBlock<N>): Animation<N> {
        val node = context.safeNode

        Animations[node]?.get(context.id)?.run {
            if (isRunning) {
                cancel()
                finalizeAnimation()
            }
        }

        context.builder()

        val animation = Animation(context)
        Animations.getOrPut(node, ::ConcurrentHashMap)[context.id] = animation

        return animation
    }

    @Synchronized
    private fun run() {
        Animations.values
            .asSequence()
            .flatMap { it.values }
            .forEach { animation ->
                catch("Error while animation run for ${animation.node}:${animation.id}") { animation.run() }

                if (!animation.isRunning) {
                    if (Animations[animation.context.safeNode]?.apply { remove(animation.id) }?.isEmpty() == true) {
                        Animations.remove(animation.context.safeNode)
                    }

                    if (!animation.isCancelled) animation.finalizeAnimation()
                }
            }
    }

    private inline val AnimationContext<*>.safeNode get() = node ?: DummyNode

    private inline val Int.rateDelay get() = (1000.0 / toDouble()).toLong()

}

fun Node.hasAnimation(id: String) = AnimationRunner.Animations[this]?.containsKey(id)

fun Node.cancelAnimation(id: String) = AnimationRunner.Animations[this]?.get(id)?.cancel()

val Node.animations get() = AnimationRunner.Animations[this] ?: emptyMap()

fun <N : Node> animate(
    id: String,
    duration: Duration,
    easing: Easing = Easings.Linear,
    node: N? = null,
    builder: AnimationContext<N>.() -> Unit
) = AnimationContext(node, id, duration, easing).build(builder)

fun <N : Node> animate(
    id: String,
    duration: Double,
    easing: Easing = Easings.Linear,
    node: N? = null,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration.seconds, easing, node, builder)

fun <N : Node> N.animate(
    id: String,
    duration: Duration,
    easing: Easing = Easings.Linear,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration, easing, this, builder)

fun <N : Node> N.animate(
    id: String,
    duration: Double,
    easing: Easing = Easings.Linear,
    builder: AnimationContext<N>.() -> Unit
) = animate(id, duration.seconds, easing, this, builder)