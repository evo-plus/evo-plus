package ru.dargen.evoplus.event.player

import net.minecraft.text.Text
import ru.dargen.evoplus.event.Event

class PlayerDisplayNameEvent(val playerName: String, var displayName: Text) : Event {
}