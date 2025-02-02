package ru.dargen.evoplus.features.misc.discord

import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.protocol.collector.data.displayName

private fun idleOr(supplier: () -> String) = fun() = if (Connector.isOnDiamondWorld) supplier() else "В меню"

private fun idleOrEvo(supplier: () -> String) = fun() = if (Connector.isOnPrisonEvo) supplier() else "???"

enum class DiscordLocationFormat(val display: String, val line: () -> String? = { null }) {

    SERVER("Тип сервера", idleOr { Connector.server.shortDisplayName }),
    SERVER_WITH_ID("Сервер", idleOr { Connector.server.shortDisplayName + "-" + Connector.server.id }),
    SERVER_ALL("Сервер и зеркало", idleOr { Connector.server.displayName }),

    LOCATION("Локация", idleOrEvo { PlayerDataCollector.location.displayName }),
    LOCATION_MIRROR("Локация с зеркалом", idleOrEvo {
        PlayerDataCollector.location.displayName + " #${Connector.server.mirror}"
    });

    override fun toString() = display

}