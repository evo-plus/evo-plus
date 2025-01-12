package ru.dargen.evoplus.protocol.collector.data

import pro.diamondworld.protocol.packet.game.LevelInfo
import ru.dargen.evoplus.protocol.collector.DataCollector

class StatisticData(collector: DataCollector<*>) {

    var level by collector.collect<Int>("level", 1)
    val nextLevel: LevelData = LevelData()

    var money by collector.collect("balance", 0.0)
    var blocks by collector.collect("blocks", 0)
    var shards by collector.collect("shards", 0)

    fun fetch(info: LevelInfo) {
        level = info.level
        blocks = info.blocks
        money = info.money

        nextLevel.fetch(info)
    }

}
