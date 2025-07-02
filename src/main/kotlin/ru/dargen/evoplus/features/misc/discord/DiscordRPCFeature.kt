package ru.dargen.evoplus.features.misc.discord

import io.github.vyfor.kpresence.ConnectionState
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.rpc.ActivityType
import net.minecraft.item.Items
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.Connector.isOnDiamondWorld
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.minecraft.PlayerName
import ru.dargen.evoplus.util.selector.enumSelector
import java.util.concurrent.TimeUnit

object DiscordRPCFeature : Feature("discord-rpc", "Discord RPC", Items.COMPARATOR) {

    private val Timestamp = System.currentTimeMillis()
    private val Client = RichClient(1297251096191828060L)

    private var enabled by settings.boolean("Отображением статуса в Discord", true) on { toggle() }
    private var nameStrategy by settings.switcher("Вид отображения имени", enumSelector<DiscordNameFormat>(2))
    private var locationStrategy by settings.switcher("Вид отображения местоположения", enumSelector<DiscordLocationFormat>(2))
    private var locationHoverStrategy by settings.switcher("Вид отображения местоположения при наведении", enumSelector<DiscordLocationFormat>(2))

    init {
        scheduleEvery(5, 5, unit = TimeUnit.SECONDS) { tryUpdate() }

        Client.on<ReadyEvent> { update() }
        Client.on<DisconnectEvent> { if (enabled) connect(shouldBlock = true) }

        toggle()
    }

    private val RichClient.isConnected get() = connectionState !== ConnectionState.DISCONNECTED

    private fun toggle() {
        runCatching {
            if (enabled) Client.connect(shouldBlock = false)
            else if (Client.isConnected) Client.shutdown()
        }.exceptionOrNull()?.printStackTrace()
    }

    private fun tryUpdate() {
        if (Client.isConnected) update()
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