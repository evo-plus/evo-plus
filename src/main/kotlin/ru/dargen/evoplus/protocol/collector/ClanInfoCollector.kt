package ru.dargen.evoplus.protocol.collector

import pro.diamondworld.protocol.packet.clan.ClanInfo
import ru.dargen.evoplus.protocol.registry.BossType

object ClanInfoCollector : DataCollector<ClanInfo>(ClanInfo::class, ClanInfo::getData) {

    val Level by collect("level", 0)
    val Members by collect("members", emptyList<String>())
    val Name by collect("name", "")
    var Bosses = emptyList<BossType>()

    init {
        collect("bosses", emptyList<String>()) { bossesId ->
            Bosses = bossesId.mapNotNull { BossType.valueOf(it) }
        }
    }
}