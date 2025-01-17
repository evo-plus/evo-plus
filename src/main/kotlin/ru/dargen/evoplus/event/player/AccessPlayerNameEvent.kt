package ru.dargen.evoplus.event.player

import ru.dargen.evoplus.event.Event

class AccessPlayerNameEvent(val player: String, var name: String) : Event {
}