package ru.dargen.evoplus

import gg.essential.universal.UScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import ru.dargen.evoplus.event.EventBus
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.feature.vigilant.FeaturesVigilant
import ru.dargen.evoplus.keybind.KeyBindings
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.render.animation.AnimationRunner
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.scheduler.Scheduler
import ru.dargen.evoplus.scheduler.after
import ru.dargen.evoplus.service.EvoPlusService
import ru.dargen.evoplus.update.UpdateResolver
import ru.dargen.evoplus.util.minecraft.command
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
        EventBus
        Scheduler
        KeyBindings

        Connector

        WorldContext
        Overlay
        AnimationRunner

        Features
        EvoPlusService

        command("mda") {
            after(1) {
                UScreen.displayScreen(FeaturesVigilant.gui())
            }
        }

        UpdateResolver.schedule()
    }

}