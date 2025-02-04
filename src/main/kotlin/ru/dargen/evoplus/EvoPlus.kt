package ru.dargen.evoplus

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import ru.dargen.evoplus.event.EventBus
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.features.alchemy.AlchemyFeature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.features.clan.ClanFeature
import ru.dargen.evoplus.features.clicker.AutoClickerFeature
import ru.dargen.evoplus.features.dev.DevFeature
import ru.dargen.evoplus.features.dungeon.DungeonFeature
import ru.dargen.evoplus.features.esp.ESPFeature
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.features.game.GoldenRushFeature
import ru.dargen.evoplus.features.misc.MiscFeature
import ru.dargen.evoplus.features.misc.RenderFeature
import ru.dargen.evoplus.features.misc.command.EvoPlusCommand
import ru.dargen.evoplus.features.misc.command.ShareCommand
import ru.dargen.evoplus.features.potion.PotionFeature
import ru.dargen.evoplus.features.rune.RuneFeature
import ru.dargen.evoplus.features.shaft.ShaftFeature
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.features.staff.StaffFeature
import ru.dargen.evoplus.features.stats.StatisticFeature
import ru.dargen.evoplus.features.text.TextFeature
import ru.dargen.evoplus.keybind.KeyBindings
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.render.animation.AnimationRunner
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.scheduler.Scheduler
import ru.dargen.evoplus.service.EvoPlusService
import ru.dargen.evoplus.update.UpdateResolver
import java.nio.file.Paths
import kotlin.io.path.createDirectories

val Logger = LoggerFactory.getLogger("EvoPlus")

object EvoPlus : ClientModInitializer {

    val Label = "§f§lEvo§6§lPlus"
    val Folder = Paths.get("evo-plus").createDirectories()

    val Container by lazy { FabricLoader.getInstance().getModContainer("evo-plus").get() }

    val Path by lazy { Container.origin.paths.first() }
    val Id by lazy { Container.metadata.id }
    val Version by lazy { Container.metadata.version.friendlyString + (if (DevEnvironment) "-dev" else "") }
    val DevEnvironment = java.lang.Boolean.getBoolean("evo-plus.dev")

    override fun onInitializeClient() {
        Connector

        WorldContext
        Overlay
        AnimationRunner

        EvoPlusService

        UpdateResolver.schedule()

        ClientCommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<FabricClientCommandSource>, _ ->
            EvoPlusCommand.register(dispatcher)
            ShareCommand.register(dispatcher)
        }
    }

    fun onPreInitializeClient() {
        EventBus
        Scheduler
        KeyBindings

        setupFeatures()
    }

    private fun setupFeatures() = Features.setup {
//        if (DevEnvironment) {
            add(DevFeature)
//        }
//
        add(AutoClickerFeature)
        add(ESPFeature)
        add(BossTimerFeature)
        add(BossFeature)
        add(StaffFeature)
        add(DungeonFeature)
        add(RuneFeature)
        add(AlchemyFeature)
        add(PotionFeature)
        add(StatisticFeature)
        add(TextFeature)
        add(FishingFeature)
        add(ClanFeature)
        add(ShaftFeature)
        add(GoldenRushFeature)
        add(RenderFeature)
        add(MiscFeature)
        add(ShareFeature)

    }

}