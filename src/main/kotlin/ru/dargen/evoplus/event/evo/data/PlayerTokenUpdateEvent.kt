package ru.dargen.evoplus.event.evo.data

import ru.dargen.evoplus.event.Event
import ru.dargen.evoplus.protocol.data.PlayerToken

class PlayerTokenUpdateEvent(val token: PlayerToken) : Event {
}