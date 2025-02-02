package ru.dargen.evoplus.protocol.collector.data

import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.json.deserializer
import ru.dargen.evoplus.util.json.gson

class GameLocation(val id: String) {

    val isEliteShaft get() = id.startsWith("shaft_tnt")
    val isShaft get() = id.startsWith("shaft")
    val level get() = if (isShaft) id.substring(6).toInt() else -1

    val isBoss get() = id.startsWith("boss")
    val bossType get() = if (isBoss) BossType.valueOf(id.substring(5)) else null

    val isDungeon get() = id.startsWith("dungeon")
    val isProceduralDungeon get() = id.startsWith("procedural_dungeon")
    val dungeonId get() = if (isDungeon) id.substring(8) else if (isProceduralDungeon) id.substring(19) else null

    val isWarp get() = !isShaft && !isBoss && !isDungeon
    val warp get() = if (isWarp) id else null

    companion object {
        init {
            gson { deserializer<GameLocation> { element, ctx -> GameLocation(element.asString) } }
        }
    }

    override fun toString(): String {
        return "GameLocation($id)"
    }

}

private val DisplayNames = mapOf(
    "pvp" to "PvP арена",
    "alchemy" to "Алхимия",
    "gorge_pvp" to "Алхимия",
    "library" to "Библиотека",
    "mine" to "Шахтерская",
    "clanArena" to "Клановая арена",
    "clan_base" to "Клановая база",
    "fish" to "Рыбалка",
    "spawn" to "Спавн",
    "wand" to "Спавн",
    "market" to "Рынок",
    "auction" to "Аукцион",
    "duels" to "Дуэли"
)

private val DungeonNames = mapOf(
    "forest" to "Дремучий лес",
    "caves" to "Пещеры",
    "catacombs" to "Катакомбы",

    "pyramid" to "Пирамида",
    "nether" to "Адские недра",
)

val GameLocation.displayName get() = when {
    isEliteShaft -> "Элитная шахта"
    isShaft -> "Шахта $level ур."
    isBoss -> "Босс ${bossType?.name ?: id}"
    isDungeon || isProceduralDungeon -> "Данж: ${DungeonNames[dungeonId]}"

    else -> DisplayNames[id] ?: id
}