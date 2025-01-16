package ru.dargen.evoplus.protocol.collector

import pro.diamondworld.protocol.packet.combo.Combo
import pro.diamondworld.protocol.packet.combo.ComboBlocks
import pro.diamondworld.protocol.packet.game.GameEvent
import pro.diamondworld.protocol.packet.game.GameEvent.EventType
import pro.diamondworld.protocol.packet.game.LevelInfo
import pro.diamondworld.protocol.packet.statistic.StatisticInfo
import ru.dargen.evoplus.api.event.evo.data.ChangeLocationEvent
import ru.dargen.evoplus.api.event.evo.data.ComboUpdateEvent
import ru.dargen.evoplus.api.event.evo.data.GameEventChangeEvent
import ru.dargen.evoplus.api.event.evo.data.LevelUpdateEvent
import ru.dargen.evoplus.api.event.fire
import ru.dargen.evoplus.protocol.collector.data.ComboData
import ru.dargen.evoplus.protocol.collector.data.EconomicData
import ru.dargen.evoplus.protocol.collector.data.GameLocation
import ru.dargen.evoplus.protocol.collector.data.PetData
import ru.dargen.evoplus.protocol.listen

object PlayerDataCollector : DataCollector<StatisticInfo>(StatisticInfo::class, StatisticInfo::getData) {

    val location by collect("gameLocation", GameLocation("spawn")) { ChangeLocationEvent.fire() }
    val pets by collect("pets", emptyList<PetData>())

    val economic = EconomicData(this)
    val statistics by collect("statistic", emptyList<Double>())
    val combo = ComboData()

    var event = EventType.NONE

    init {
        listen<GameEvent> {
            if (event !== it.type) {
                GameEventChangeEvent(event, it.type).fire()
                event = it.type
            }
        }
        listen<Combo> {
            combo.fetch(it)
            ComboUpdateEvent(combo).fire()
        }
        listen<ComboBlocks> {
            combo.fetch(it)
            ComboUpdateEvent(combo).fire()
        }
        listen<LevelInfo> {
            val previousLevel = economic.nextLevel.copy()
            economic.fetch(it)
            LevelUpdateEvent(previousLevel, economic.nextLevel).fire()
        }
    }

}