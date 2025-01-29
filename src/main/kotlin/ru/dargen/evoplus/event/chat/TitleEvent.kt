package ru.dargen.evoplus.event.chat

import net.minecraft.text.Text
import ru.dargen.evoplus.event.Event

class TitleEvent(var title: Text, val subtitle: Boolean) : Event {
}