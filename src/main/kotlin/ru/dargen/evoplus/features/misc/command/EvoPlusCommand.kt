package ru.dargen.evoplus.features.misc.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import ru.dargen.evoplus.feature.FeaturesSettings
import ru.dargen.evoplus.util.minecraft.Client

object EvoPlusCommand {

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        registerCommand(dispatcher)
        dispatcher.register(ClientCommandManager.literal("ep").executes { dispatcher.execute("evoplus", it.source) })
    }

    fun registerCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>): LiteralCommandNode<FabricClientCommandSource> {
        return dispatcher.register(
            ClientCommandManager.literal("evoplus")
                .executes {
                    Client.currentScreen?.close()
                    FeaturesSettings.open(2)
                    1
                }
        )
    }
}