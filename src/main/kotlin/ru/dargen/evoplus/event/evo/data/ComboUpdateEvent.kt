package ru.dargen.evoplus.event.evo.data

import ru.dargen.evoplus.event.Event
import ru.dargen.evoplus.protocol.collector.data.ComboData

class ComboUpdateEvent(val combo: ComboData) : Event {
}