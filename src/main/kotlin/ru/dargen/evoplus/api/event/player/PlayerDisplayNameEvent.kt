package ru.dargen.evoplus.api.event.player

import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.Event

class PlayerDisplayNameEvent(val playerName: String, var displayName: Text) : Event {
}