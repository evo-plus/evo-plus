package ru.dargen.evoplus.features.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import ru.dargen.evoplus.feature.screen.FeatureScreen
import ru.dargen.evoplus.scheduler.after
import ru.dargen.evoplus.util.minecraft.Client

object EvoPlusCommand {

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        registerCommand(dispatcher)
        dispatcher.register(literal("ep").executes { dispatcher.execute("evoplus", it.source) })
    }

    fun registerCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>): LiteralCommandNode<FabricClientCommandSource> {
        return dispatcher.register(
            literal("evoplus")
                .executes {
                    Client.currentScreen?.close()
                    after(2) { FeatureScreen().open() }
                    1
                }
        )
    }
}
