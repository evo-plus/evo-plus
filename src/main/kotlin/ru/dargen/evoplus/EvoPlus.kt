package ru.dargen.evoplus

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import ru.dargen.evoplus.event.EventBus
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.features.command.EvoPlusCommand
import ru.dargen.evoplus.features.command.ShareCommand
import ru.dargen.evoplus.keybind.KeyBindings
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.render.animation.AnimationRunner
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.scheduler.Scheduler
import ru.dargen.evoplus.service.EvoPlusService
import ru.dargen.evoplus.update.UpdateResolver

val Logger = LoggerFactory.getLogger("EvoPlus")

object EvoPlus : ClientModInitializer {

    val Label = "§f§lEvo§6§lPlus"
    val LabelText = Text.of(Label)

    val Container by lazy { FabricLoader.getInstance().getModContainer("evo-plus").get() }

    val Path by lazy { Container.origin.paths.first() }
    val Id by lazy { Container.metadata.id }
    val Version by lazy { Container.metadata.version.friendlyString + (if (DevEnvironment) "-dev" else "") }
    val DevEnvironment = true// java.lang.Boolean.getBoolean("evo-plus.dev")

    override fun onInitializeClient() {
        EventBus
        Scheduler
        KeyBindings

        Connector

        WorldContext
        Overlay
        AnimationRunner

        Features
        EvoPlusService

        UpdateResolver.schedule()

        ClientCommandRegistrationCallback.EVENT.register { dispatcher: CommandDispatcher<FabricClientCommandSource>, _ ->
            EvoPlusCommand.register(dispatcher)
            ShareCommand.register(dispatcher)
        }
    }

}