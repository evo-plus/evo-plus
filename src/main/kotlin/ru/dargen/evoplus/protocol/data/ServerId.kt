package ru.dargen.evoplus.protocol.data

import pro.diamondworld.protocol.packet.ServerInfo


data class ServerId(val name: String, val id: Int, val mirror: Int) {

    val displayName
        get() = when (name) {
            "HUB" -> "Хаб"
            "PRISONEVO" -> "PrisonEvo-$id #$mirror"
            "UNKNOWN" -> "Где?"
            else -> toString()
        }

    val shortDisplayName get() = when (name) {
        "HUB" -> "Хаб"
        "PRISONEVO" -> "PrisonEvo"
        "UNKNOWN" -> "Где?"
        else -> toString()
    }

    override fun toString() = "$name-$id-$mirror"

    companion object {

        private val Regex = "^([A-Z]*)(\\d*)\$".toRegex()

        val HUB = ServerId("HUB", 0, 0)
        val UNKNOWN = ServerId("UNKNOWN", 0, 0)

        fun ServerInfo.asId(): ServerId {
            val (name, id) = Regex.find(serverName)?.groupValues?.drop(1) ?: listOf("HUB", "0")
            return ServerId(name, id.toIntOrNull() ?: 0, serverId)
        }

    }
}
