package ru.dargen.evoplus.api.event.evo.data

import ru.dargen.evoplus.api.event.Event
import ru.dargen.evoplus.protocol.collector.data.ComboData

class ComboUpdateEvent(val combo: ComboData) : Event {
}