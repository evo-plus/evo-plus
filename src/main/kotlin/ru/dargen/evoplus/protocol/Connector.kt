package ru.dargen.evoplus.protocol

import pro.diamondworld.protocol.packet.ServerInfo
import pro.diamondworld.protocol.packet.VerificationToken
import ru.dargen.evoplus.api.event.evo.EvoJoinEvent
import ru.dargen.evoplus.api.event.evo.EvoQuitEvent
import ru.dargen.evoplus.api.event.evo.data.PlayerTokenUpdateEvent
import ru.dargen.evoplus.api.event.fire
import ru.dargen.evoplus.api.event.network.ChangeServerEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.mixin.render.hud.PlayerListHudAccessor
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.protocol.collector.StatisticCollector
import ru.dargen.evoplus.protocol.data.PlayerToken
import ru.dargen.evoplus.protocol.data.PlayerToken.Companion.parse
import ru.dargen.evoplus.protocol.data.ServerId
import ru.dargen.evoplus.protocol.data.ServerId.Companion.asId
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.protocol.registry.FishingSpot
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.protocol.registry.PetType
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.protocol.registry.StaffType
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

    val isOnPrisonEvo get() = server.name == "PRISONEVO"
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
        StatisticCollector
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