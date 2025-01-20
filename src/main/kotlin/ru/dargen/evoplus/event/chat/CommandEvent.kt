package ru.dargen.evoplus.event.chat

import ru.dargen.evoplus.event.CancellableEvent

class CommandEvent(var command: String) : CancellableEvent() {

    val name: String by lazy { command.split(" ")[0] }
    val args: List<String> by lazy { command.split(" ").drop(1) }

}