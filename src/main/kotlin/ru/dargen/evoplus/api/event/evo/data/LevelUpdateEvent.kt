package ru.dargen.evoplus.api.event.evo.data

import ru.dargen.evoplus.api.event.Event
import ru.dargen.evoplus.protocol.collector.data.LevelData

data class LevelUpdateEvent(val previousLevel: LevelData, val level: LevelData) : Event