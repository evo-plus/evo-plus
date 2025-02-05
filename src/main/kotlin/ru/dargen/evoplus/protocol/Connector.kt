package ru.dargen.evoplus.protocol

import pro.diamondworld.protocol.packet.ServerInfo
import pro.diamondworld.protocol.packet.VerificationToken
import ru.dargen.evoplus.event.evo.EvoJoinEvent
import ru.dargen.evoplus.event.evo.EvoQuitEvent
import ru.dargen.evoplus.event.evo.data.PlayerTokenUpdateEvent
import ru.dargen.evoplus.event.fire
import ru.dargen.evoplus.event.network.ChangeServerEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.mixin.render.hud.PlayerListHudAccessor
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.protocol.data.PlayerToken
import ru.dargen.evoplus.protocol.data.PlayerToken.Companion.parse
import ru.dargen.evoplus.protocol.data.ServerId
import ru.dargen.evoplus.protocol.data.ServerId.Companion.asId
import ru.dargen.evoplus.protocol.registry.*
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.kotlin.invoke
import ru.dargen.evoplus.util.minecraft.Client
import java.util.concurrent.TimeUnit

object Connector {

    var server = ServerId.HUB
        private set(value) {
            if (value.name != field.name && value.name == "PRISONEVO") EvoJoinEvent.fire()
            else if (field.name == "PRISONEVO") EvoQuitEvent.fire()

            field = value
        }
    var token: PlayerToken = PlayerToken.Invalid()
        private set

    val isOnPrisonEvo get() = isOnDiamondWorld && server.name == "PRISONEVO"
    var isOnDiamondWorld = false
        private set

    init {
        EvoPlusProtocol
        scheduleEvery(unit = TimeUnit.SECONDS) {
            //rofl ;d
            isOnDiamondWorld = Client.inGameHud
                ?.playerListHud<PlayerListHudAccessor>()
                ?.footer?.string
                ?.contains("diamondworld.pro") == true
        }

        on<ChangeServerEvent> { server = ServerId.Companion.HUB }
        listen<ServerInfo> { server = it.asId() }

        listen<VerificationToken> {
            token = it.parse()
            PlayerTokenUpdateEvent(token).fire()
        }

        initCollectors()
        initRegistries()
    }

    fun initCollectors() {
        PlayerDataCollector
        ClanInfoCollector
    }

    fun initRegistries() {
        BossType
        StaffType
        PotionType
        PetType
        FishingSpot
        AbilityType
        HourlyQuestType
    }

}