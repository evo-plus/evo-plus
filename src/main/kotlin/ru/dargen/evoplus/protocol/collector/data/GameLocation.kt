package ru.dargen.evoplus.protocol.collector.data

import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.json.deserializer
import ru.dargen.evoplus.util.json.gson

class GameLocation(val id: String) {

    val isShaft get() = id.startsWith("shaft")
    val level get() = if (isShaft) id.substring(6).toInt() else -1

    val isBoss get() = id.startsWith("boss")
    val bossType get() = if (isBoss) BossType.valueOf(id.substring(5)) else null

    val isWarp get() = !isShaft && !isBoss
    val warp get() = if (isWarp) id else null

    val isFish get() = id == "fish"
    val isDungeon get() = "dungeon" in id
    val isSpawn get() = id == "spawn"

    companion object {
        init {
            gson { deserializer<GameLocation> { element, ctx -> GameLocation(element.asString) } }
        }
    }

    override fun toString(): String {
        return "GameLocation($id)"
    }

}