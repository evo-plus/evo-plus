package ru.dargen.evoplus.features.misc.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.printMessage

object ShareCommand {

    val typeList = ShareType.entries.map { it.name.lowercase() }

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val node = registerCommand(dispatcher)

        dispatcher.register(
            literal("share")
                .executes { context ->
                    dispatcher.execute("send", context.source)
                }
                .redirect(node)
        )
    }

    fun registerCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>): LiteralCommandNode<FabricClientCommandSource> {
        return dispatcher.register(
            literal("send")
                .then(
                    argument("type", StringArgumentType.word())
                        .suggests { _, builder ->
                            typeList.forEach { builder.suggest(it) }
                            builder.buildFuture()
                        }
                        .then(
                            argument("target", StringArgumentType.greedyString())
                                .suggests { context, builder ->
                                    val input = context.input.substring(context.input.lastIndexOf(" ") + 1)
                                    val players = Client.networkHandler?.playerList?.map { it.profile.name }
                                        ?.filter {
                                            it != context.source.player.name.string && it.lowercase()
                                                .startsWith(input.lowercase())
                                        } ?: emptyList()

                                    players.forEach { builder.suggest(it) }
                                    builder.suggest("@")
                                    builder.buildFuture()
                                }
                                .executes { context ->
                                    val type = StringArgumentType.getString(context, "type").lowercase()
                                    val target = StringArgumentType.getString(context, "target")
                                    val displayType = try {
                                        ShareType.valueOf(type.uppercase()).displayName
                                    } catch (_: IllegalArgumentException) {
                                        printMessage("§cНеверный тип! Доступные типы: $typeList")
                                        return@executes 0
                                    }

                                    if (!Connector.isOnPrisonEvo) {
                                        printMessage("§cЭта команда работает только на сервере PrisonEvo!")
                                        return@executes 0
                                    }

                                    if (target == context.source.player.name.string) {
                                        printMessage("§cВы не можете взаимодействовать с собой!")
                                        return@executes 0
                                    }

                                    if (ClanInfoCollector.Name.isEmpty()) {
                                        printMessage("§cВы не состоите в клане!")
                                        return@executes 0
                                    }

                                    if (target == "@") {
                                        printMessage("§aВы поделились $displayType с кланом!")
                                        ShareFeature.shares[type]!!.share(null)
                                    } else {
//                                        printMessage("§aВы поделились $displayType с игроком $target!")
                                        ShareFeature.shares[type]!!.share(target)
                                    }
                                    1
                                }
                        )
                        .executes { context ->
                            printMessage("§cОтсутствует аргумент цель!")
                            0
                        }
                )
                .executes { context ->
                    printMessage("§cИспользование: /send <Тип> <Цель>")
                    0
                }
        )
    }
}
