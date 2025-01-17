package ru.dargen.evoplus.event.evo.data

import ru.dargen.evoplus.event.Event
import ru.dargen.evoplus.protocol.collector.data.LevelData

data class LevelUpdateEvent(val previousLevel: LevelData, val level: LevelData) : Event