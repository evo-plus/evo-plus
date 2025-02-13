package ru.dargen.evoplus.event

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import ru.dargen.evoplus.scheduler.async
import ru.dargen.evoplus.util.kotlin.cast
import java.util.concurrent.ConcurrentHashMap

object EventBus {

    private val handlers: MutableMap<Class<*>, MutableSet<EventHandlerData<*>>> = ConcurrentHashMap()

    private val bakedHandlers: MutableMap<Class<*>, EventHandler<*>> = Object2ObjectOpenHashMap()
    private val bakedAsyncHandlers: MutableMap<Class<*>, EventHandler<*>> = Object2ObjectOpenHashMap()

    private fun bake(clazz: Class<*>) {
        fun bake(async: Boolean) = handlers[clazz]!!
            .filter { it.async == async }
            .sortedByDescending { it.priority }
            .map { it.throwSafeHandler }
            .filterIsInstance<EventHandler<Any>>()
            .reduceOrNull { acc, handler -> { acc(); handler() } }

        bake(true)?.let { bakedAsyncHandlers[clazz] = it }
        bake(false)?.let { bakedHandlers[clazz] = it }
    }

    fun <E : Event> register(type: Class<E>, priority: Int, async: Boolean, handler: EventHandler<E>) {
        handlers.getOrPut(type, ::ObjectOpenHashSet).add(EventHandlerData(type, handler, async, priority))
        bake(type)
    }

    fun <E : Event> fire(event: E) = event.apply {
        fun EventHandler<*>.handle() = this.cast<EventHandler<E>>()(event)

        bakedHandlers[event.javaClass]?.handle()
        async { bakedAsyncHandlers[event.javaClass]?.handle() }
    }

    fun <E : CancellableEvent> fireResult(event: E) = !fire(event).isCancelled

}

inline fun <reified E : Event> on(
    priority: Int = 0, async: Boolean = false,
    noinline handler: EventHandler<E>,
) = EventBus.register(E::class.java, priority, async, handler)