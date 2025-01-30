package ru.dargen.evoplus.features.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import ru.dargen.evoplus.util.minecraft.printMessage

//    // TODO: rewrite this class
class CommandBuilder(
    private val dispatcher: CommandDispatcher<FabricClientCommandSource>,
    private val primaryAlias: String
) {

//    private val builder: LiteralArgumentBuilder<FabricClientCommandSource> = literal(primaryAlias)
//    private val aliases: MutableList<String> = mutableListOf()
//    private var current: RequiredArgumentBuilder<FabricClientCommandSource, *>? = null
//
//    fun alias(vararg aliases: String): CommandBuilder {
//        this.aliases.addAll(aliases)
//        return this
//    }
//
//    fun <T : Any> argument(
//        name: String,
//        type: ArgumentType<T>,
//        suggestions: SuggestionProvider<FabricClientCommandSource>? = null,
//        block: ((CommandContext<FabricClientCommandSource>) -> Unit)? = null
//    ): CommandBuilder {
//        val argument: RequiredArgumentBuilder<FabricClientCommandSource, *> =
//            RequiredArgumentBuilder.argument(name, type)
//        if (suggestions != null) argument.suggests(suggestions)
//        // TODO: fix executes
//        if (block != null)
//            argument.executes { context ->
//                try {
//                    block(context)
//                    1
//                } catch (e: Exception) {
//                    printMessage("§cОшибка выполнения команды: ${e.message}")
//                    0
//                }
//            }
//        current = current?.then(argument) ?: argument
//        return this
//    }
//
//    fun executes(block: (CommandContext<FabricClientCommandSource>) -> Unit): CommandBuilder {
//        builder.executes { context ->
//            try {
//                block(context)
//                1
//            } catch (e: Exception) {
//                printMessage("§cОшибка выполнения команды: ${e.message}")
//                0
//            }
//        }
//        return this
//    }
//
//    fun then(argument: ArgumentBuilder<FabricClientCommandSource, *>): CommandBuilder {
//        builder.then(argument)
//        return this
//    }
//
//    fun register() {
//        dispatcher.register(builder.then(current))
//        aliases.forEach { alias ->
//            val aliasBuilder = literal<FabricClientCommandSource>(alias).executes(builder.command)
//            builder.arguments.forEach { aliasBuilder.then(it) }
//            dispatcher.register(aliasBuilder)
//        }
//    }
//
//    companion object {
//        fun create(dispatcher: CommandDispatcher<FabricClientCommandSource>, commandName: String): CommandBuilder {
//            return CommandBuilder(dispatcher, commandName)
//        }
//    }
}