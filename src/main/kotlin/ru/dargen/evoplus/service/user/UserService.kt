package ru.dargen.evoplus.service.user

import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.api.event.evo.data.PlayerTokenUpdateEvent
import ru.dargen.evoplus.api.event.network.ChangeServerEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.util.collection.takeIfNotEmpty
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.newSetCacheExpireAfterAccess
import ru.dargen.evoplus.util.rest.controller
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration.Companion.minutes

object UserService {

    private val UserController = controller<UserController>()
    private val activeUsers = newSetCacheExpireAfterAccess<String>(2.minutes)

    init {
        on<PlayerTokenUpdateEvent> { if (token.isWorking) tryUpdate() }

        on<ChangeServerEvent> { tryFetchActiveUsers() }
        scheduleEvery(20, 20, unit = SECONDS) { tryFetchActiveUsers() }
    }

    fun isActiveUser(name: String) = name.lowercase() in activeUsers

    private fun tryUpdate() {
        if (Connector.isOnDiamondWorld && Connector.token.isWorking) {
            runCatching {
                UserController.update(Connector.token.token, EvoPlus.Version, Connector.server.toString())
            }.onFailure { Logger.error("Error while updating user", it) }
        }
    }

    private fun tryFetchActiveUsers() {
        if (Connector.isOnDiamondWorld) {
            fetchActiveUsers()
        }
    }

    private fun fetchActiveUsers(
        players: Collection<String> = Client.networkHandler?.playerList?.mapNotNull { it?.profile?.name } ?: emptySet(),
    ) {
        val players = players
            .filterNot { '§' in it || it.isBlank() }
            .map(String::lowercase)
            .filter { !isActiveUser(it).apply { if (this) activeUsers.add(it) } }
            .takeIfNotEmpty() ?: return

        runCatching { UserController.filterActive(players) }
            .onFailure { Logger.error("Error while fetch active users", it) }
            .onSuccess {
                activeUsers.addAll(it.map(String::lowercase))
                activeUsers.add(Client.session.username.lowercase())
                Logger.info("${it.size + 1}/${players.size} with EvoPlus!")
            }
    }

}