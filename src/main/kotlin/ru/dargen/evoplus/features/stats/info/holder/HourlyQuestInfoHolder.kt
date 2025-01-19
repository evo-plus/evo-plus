package ru.dargen.evoplus.features.stats.info.holder

import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.currentMillis

class HourlyQuestInfoHolder(type: HourlyQuestType, info: HourlyQuestInfo.HourlyQuest) {

    val id = type.id
    val name = type.name
    val type = type.type
    val lore = type.lore
    val needed = type.needed
    val progress = info.progress

    val remained = info.remained
    val timestamp = currentMillis + remained

    val isCompleted get() = progress >= needed
    val isClaimed get() = progress < 0
    val isAvailable get() = !isClaimed && !isCompleted

}