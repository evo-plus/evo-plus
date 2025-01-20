package ru.dargen.evoplus.util.minecraft

import ru.dargen.evoplus.event.chat.CommandEvent
import ru.dargen.evoplus.event.on
//TODO: make with brigadier
fun command(
    vararg aliases: String,
    usage: String? = null,
    argumentsCount: Int = -1,
    block: (List<String>) -> Unit,
) {
    on<CommandEvent> {
        if (name.lowercase() in aliases) {
            cancel()
            if (argumentsCount != args.size) {
                printMessage("Использование: /${aliases.first()} $usage")
            } else runCatching { block(args) }.onFailure { printMessage("Использование: /${aliases.first()} $usage") }
        }
    }
}