package ru.dargen.evoplus.event.chat

import net.minecraft.text.Text
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
data class ChatReceiveEvent(var message: Text) : CancellableEvent() {

    val text get() = message.string

}