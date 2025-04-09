package ru.dargen.evoplus.service.user

import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.event.evo.data.PlayerTokenUpdateEvent
import ru.dargen.evoplus.event.network.ChangeServerEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.service.user.model.UserStatisticModel
import ru.dargen.evoplus.util.collection.takeIfNotEmpty
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.PlayerName
import ru.dargen.evoplus.util.minecraft.isNPCName
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import ru.dargen.evoplus.util.newSetCacheExpireAfterAccess
import ru.dargen.evoplus.util.rest.controller
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration.Companion.minutes

object UserService {

    private val UserController = controller<UserController>()
    private val activeUsers = newSetCacheExpireAfterAccess<String>(2.minutes)
    private val userNames = newCacheExpireAfterAccess<String, String>(1.minutes)

    init {
        on<PlayerTokenUpdateEvent> { tryUpdate() }

        on<ChangeServerEvent> { tryFetchUserData() }
        scheduleEvery(1, 1, unit = MINUTES) { tryUpdateStatistic() }
        scheduleEvery(20, 20, unit = SECONDS) { tryFetchUserData() }
    }

    fun isActiveUser(name: String) = name.equals(PlayerName, true) || name.lowercase() in activeUsers

    fun hasDisplayName(player: String) = player in userNames.asMap()

    fun getDisplayName(player: String) = userNames.getIfPresent(player.lowercase())

    private fun tryUpdate() {
        if (Connector.isOnDiamondWorld && Connector.token.isWorking) {
            runCatching {
                UserController.update(Connector.token.token, EvoPlus.Version, Connector.server.toString())
            }.onFailure { Logger.error("Error while updating user", it) }
        }
    }

    private fun tryUpdateStatistic() {
        if (Connector.isOnPrisonEvo && Connector.token.isWorking) {
            UserController.updateStatistic(
                Connector.token.token, Connector.server.id, UserStatisticModel(
                    PlayerDataCollector.economic.level,
                    PlayerDataCollector.economic.blocks,
                    PlayerDataCollector.economic.money,
                    PlayerDataCollector.economic.shards.toDouble(),
                    PlayerDataCollector.location.id,
                    PlayerDataCollector.statisticsRaw
                )
            )
        }
    }

    private fun tryFetchUserData() {
        if (Connector.isOnPrisonEvo) {
            fetchActiveUsers()
            fetchUsersDisplayNames()
        }
    }

    private fun fetchActiveUsers(
        players: Collection<String> = Client.networkHandler?.playerList?.mapNotNull { it?.profile?.name } ?: emptySet(),
    ) {
        val players = players
            .filterNot { it.isNPCName }
            .filter { !isActiveUser(it).apply { if (this) activeUsers.add(it.lowercase()) } }
            .takeIfNotEmpty() ?: return

        runCatching { UserController.filterActive(players) }
            .onFailure { Logger.error("Error while fetch active users", it) }
            .onSuccess {
                activeUsers.addAll(it.map(String::lowercase))
                Logger.info("${it.size + 1}/${players.size} with EvoPlus!")
            }
    }

    private fun fetchUsersDisplayNames(
        players: Collection<String> = Client.networkHandler?.playerList?.mapNotNull { it?.profile?.name } ?: emptySet(),
    ) {
        val players = players
            .filterNot { it.isNPCName }
            .takeIfNotEmpty() ?: return

        runCatching { UserController.getDisplayNames(players) }
            .onFailure { Logger.error("Error while fetch users names", it) }
            .onSuccess {
                userNames.putAll(
                    it.map { (player, name) -> player.lowercase() to name.replace("%name%", player) }.toMap()
                )
            }
    }

}