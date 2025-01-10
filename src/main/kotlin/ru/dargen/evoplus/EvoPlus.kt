package ru.dargen.evoplus

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import ru.dargen.evoplus.api.event.EventBus
import ru.dargen.evoplus.api.keybind.KeyBindings
import ru.dargen.evoplus.api.render.animation.AnimationRunner
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.WorldContext
import ru.dargen.evoplus.api.scheduler.Scheduler
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.protocol.EvoPlusProtocol
import ru.dargen.evoplus.service.EvoPlusService
import ru.dargen.evoplus.update.UpdateResolver

val Logger = LoggerFactory.getLogger("EvoPlus")

object EvoPlus : ClientModInitializer {

    val Label = "§f§lEvo§6§lPlus"
    val LabelText = Text.of(Label)

    val Container by lazy { FabricLoader.getInstance().getModContainer("evo-plus").get() }

    val Path by lazy { Container.origin.paths.first() }
    val Id by lazy { Container.metadata.id }
    val Version by lazy { Container.metadata.version }
    val VersionString by lazy { Version.friendlyString }
    val DevEnvironment =java.lang.Boolean.getBoolean("evo-plus.dev")

    override fun onInitializeClient() {
        EventBus
        Scheduler
        KeyBindings

        EvoPlusProtocol

        WorldContext
        Overlay
        AnimationRunner

        Features
        EvoPlusService

        UpdateResolver.schedule()
    }

}