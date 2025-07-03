package ru.dargen.evoplus.util.minecraft

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.network.packet.Packet
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvent
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.extension.MinecraftClientExtension
import ru.dargen.evoplus.mixin.MinecraftClientAccessor
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.text.print


val Client get() = MinecraftClient.getInstance()
val ClientExtension get() = Client.cast<MinecraftClientExtension>()
val ClientAccessor get() = Client.cast<MinecraftClientAccessor>()

val PlayerName get() = Client?.session?.username ?: ""
val Player get() = Client?.player
val World get() = Client?.world
val InteractionManager get() = Client?.interactionManager

val PlayersProfiles
    get() = Client?.networkHandler?.playerList
        ?.mapNotNull { it.profile }
        ?.filter { it.name?.isNPCName == false }
        .orEmpty()
val WorldEntities get() = World?.entities?.filterNotNull() ?: emptyList()
val TargetEntity get() = Client?.crosshairTarget?.safeCast<EntityHitResult>()?.entity
val TargetBlock get() = Client?.crosshairTarget?.safeCast<BlockHitResult>()?.blockPos
val LoadedChunks
    get() = buildList {
        val viewDist = Client?.options?.viewDistance?.value ?: return@buildList

        for (x in -viewDist..viewDist) {
            for (z in -viewDist..viewDist) {
                val chunk = World?.chunkManager
                    ?.getWorldChunk(Player!!.x.toInt() / 16 + x, Player!!.z.toInt() / 16 + z) ?: continue

                add(chunk)
            }
        }
    }
val BlockEntities
    get() = LoadedChunks
        .map { it.blockEntities.values }
        .flatten()


val CurrentScreenHandler get() = Player?.currentScreenHandler
val CurrentScreen get() = Client?.currentScreen
val CurrentContainer get() = CurrentScreen?.safeCast<HandledScreen<*>>()

val WindowInitialized get() = Client.window != null
val Window get() = Client.window

val MousePosition
    get() = if (!WindowInitialized) Vector3() else Vector3(
        Client.mouse.x * Window.scaledWidth / Window.width,
        Client.mouse.y * Window.scaledHeight / Window.height,
    ).fixNaN()

fun postToMainThread(runnable: () -> Unit) =
    Client.cast<MinecraftClientAccessor>().renderTaskQueue.add(runnable)

fun forceInMainThread(runnable: () -> Unit) {
    if (!MinecraftClient.getInstance().isOnThread) postToMainThread(runnable) else runnable()
}

fun playSound(event: SoundEvent) = Player?.playSound(event, 1f, 1f)
fun playSound(event: RegistryEntry.Reference<SoundEvent>) = playSound(event.value())

fun printMessage(message: String?) = Player?.sendMessage("${EvoPlus.Label}§8: §f$message".asText(), false)

fun printHoveredCommandMessage(message: String, hover: String, command: String) =
    Text.literal("${EvoPlus.Label}§8: §a$message").run {
        style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.asText()))
            .withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command))

        Player?.sendMessage(this, false)
    }

fun sendChatMessage(message: String) =
    Player?.networkHandler?.sendChatMessage(message.replace('§', '&'))

fun sendClanMessage(message: String) {
    if (Connector.isOnPrisonEvo && ClanInfoCollector.Name.isNotEmpty()) sendChatMessage("@$message")
}

fun sendCommand(command: String) = Player?.networkHandler?.sendChatCommand(command)

fun sendPacket(packet: Packet<*>) = Player?.networkHandler?.sendPacket(packet)
