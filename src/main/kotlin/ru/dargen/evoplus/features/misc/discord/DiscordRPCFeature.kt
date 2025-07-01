package ru.dargen.evoplus.features.misc.discord

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import io.github.vyfor.kpresence.ConnectionState
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.rpc.ActivityType
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.Connector.isOnDiamondWorld
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.minecraft.PlayerName
import java.util.concurrent.TimeUnit

object DiscordRPCFeature : Feature(name = "Discord RPC") {

    private val Timestamp = System.currentTimeMillis()
    private val Client = RichClient(1297251096191828060L)

    private var enabled = true
    private var nameStrategy = DiscordNameFormat.NAME_WITH_LEVEL
    private var locationStrategy = DiscordLocationFormat.SERVER_ALL
    private var locationHoverStrategy = DiscordLocationFormat.SERVER_ALL

    override fun CategoryBuilder.setup() {
        switch(::enabled, "Отображение", "Управляет отображением статуса в Discord", observeInit = false) { toggle() }
        selector(::nameStrategy, "Имя", "Вид отображения имени")
        selector(::locationStrategy, "Местоположение", "Вид отображения местоположения")
        selector(::locationHoverStrategy, "Местоположение при наведении", "Вид отображения местоположения при наведении")
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
        state = nameStrategy.line()
        details = locationStrategy.line()

        party { id = "${Connector.server}" }
        timestamps { start = Timestamp }

        assets {
            largeImage = "logo"
            largeText = "EvoPlus ${EvoPlus.Version}"

            if (isOnDiamondWorld) {
                smallImage = "https://mc-heads.net/avatar/$PlayerName"
                smallText = locationHoverStrategy.line()
            }
        }

        button("Скачать EvoPlus", "https://modrinth.com/mod/evoplus")
    }


}