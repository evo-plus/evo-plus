package ru.dargen.evoplus.features.misc.discord

import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.util.minecraft.PlayerName

enum class DiscordNameFormat(val display: String, val line: () -> String? = { null }) {

    EMPTY("Не отображать"),
    ONLY_NAME("Только имя", { PlayerName }),
    NAME_WITH_LEVEL("Имя с уровнем", {
        PlayerName + (if (Connector.isOnPrisonEvo) " [${PlayerDataCollector.economic.level} ур.]" else "")
    });

    override fun toString() = display

}