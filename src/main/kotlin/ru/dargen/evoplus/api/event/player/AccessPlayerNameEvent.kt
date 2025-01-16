package ru.dargen.evoplus.api.event.player

import ru.dargen.evoplus.api.event.Event

class AccessPlayerNameEvent(val player: String, var name: String) : Event {
}