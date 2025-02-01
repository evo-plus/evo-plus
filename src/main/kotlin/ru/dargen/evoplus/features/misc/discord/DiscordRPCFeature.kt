package ru.dargen.evoplus.features.misc.discord

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import io.github.vyfor.kpresence.ConnectionState
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.rpc.ActivityType
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.minecraft.PlayerName
import java.util.concurrent.TimeUnit

object DiscordRPCFeature : Feature(name = "Discord RPC") {

    private val Timestamp = System.currentTimeMillis()
    private val Client = RichClient(1297251096191828060L)

    private var enabled = true
    private var showName = true
    private var showLevel = true
    private var showServer = true

    override fun CategoryBuilder.setup() {
        switch(::enabled, "Отображение", "Управляет отображением статуса в Discord", observeInit = false) { toggle() }
        switch(::showName, "Отображать имя", "Включает отображение имени в Discord")
        switch(::showLevel, "Отображать уровень", "Включает отображение уровня в Discord")
        switch(::showServer, "Отображать сервер", "Включает отображение сервера в Discord")
    }

    override fun initialize() {
        scheduleEvery(5, 5, unit = TimeUnit.SECONDS) { tryUpdate() }

        Client.on<ReadyEvent> { update() }
        Client.on<DisconnectEvent> { if (enabled) connect(shouldBlock = true) }

        toggle()
    }

    val RichClient.isConnected get() = connectionState !== ConnectionState.DISCONNECTED

    private fun toggle() {
        if (enabled) Client.connect(shouldBlock = false)
        else if (Client.isConnected) Client.shutdown()
    }

    private fun tryUpdate() {
        if (Client.isConnected) {
            update()
        }
    }

    private fun update() = Client.update {
        type = ActivityType.GAME
        if (showName) {
            state = PlayerName
            if (Connector.isOnPrisonEvo && showLevel) state += " [${PlayerDataCollector.economic.level} ур.]"
        }
        details = when {
            Connector.isOnPrisonEvo -> if (showServer) Connector.server.displayName else "PrisonEvo"
            Connector.isOnDiamondWorld -> if (showServer) Connector.server.displayName else "DiamondWorld"
            else -> "В меню"
        }

        timestamps { start = Timestamp }

        assets {
            largeImage = "logo"
            largeText = "EvoPlus ${EvoPlus.Version}"
        }

        button("Скачать EvoPlus", "https://modrinth.com/mod/evoplus")
    }


}