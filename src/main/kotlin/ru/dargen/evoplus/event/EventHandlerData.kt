package ru.dargen.evoplus.event

import ru.dargen.evoplus.util.catch


typealias EventHandler<E> = E.() -> Unit

data class EventHandlerData<E>(
    val type: Class<E>, val handler: EventHandler<E>,
    val async: Boolean, val priority: Int = 0,
) {

    val throwSafeHandler get() = { event: E -> catch("Error while event dispatch ${type.simpleName}") { handler(event) } }

}
