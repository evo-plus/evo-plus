package ru.dargen.evoplus.event.evo.data

import pro.diamondworld.protocol.packet.game.GameEvent
import ru.dargen.evoplus.event.Event

class GameEventChangeEvent(val old: GameEvent.EventType, val new: GameEvent.EventType) : Event