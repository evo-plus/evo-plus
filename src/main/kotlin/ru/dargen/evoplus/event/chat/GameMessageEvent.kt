package ru.dargen.evoplus.event.chat

import net.minecraft.text.Text
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
data class GameMessageEvent(val message: Text, val overlay: Boolean) : CancellableEvent() {

    val text get() = message.string

}