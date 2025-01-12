package ru.dargen.evoplus.api.event.evo.data

import ru.dargen.evoplus.api.event.Event
import ru.dargen.evoplus.protocol.data.PlayerToken

class PlayerTokenUpdateEvent(val token: PlayerToken) : Event {
}