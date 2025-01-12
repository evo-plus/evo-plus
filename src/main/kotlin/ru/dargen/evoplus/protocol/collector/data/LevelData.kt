package ru.dargen.evoplus.protocol.collector.data

import pro.diamondworld.protocol.packet.game.LevelInfo

data class LevelData(
    var money: Double = .0, var blocks: Int = 0,
    var isMaxLevel: Boolean = true, var isCompleted: Boolean = false,
) {

    fun fetch(info: LevelInfo) {
        money = info.requiredMoney
        blocks = info.requiredBlocks
        isMaxLevel = info.isMaxLevel
        isCompleted = info.blocks >= info.requiredBlocks && info.money >= info.requiredMoney
    }

}
