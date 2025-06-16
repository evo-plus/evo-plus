package ru.dargen.evoplus.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.packet.UnknownCustomPayload
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import pro.diamondworld.protocol.ProtocolRegistry
import pro.diamondworld.protocol.util.BufUtil
import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.event.network.CustomPayloadEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.minecraft.Client
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

typealias Handler<T> = (T) -> Unit
typealias RawHandler = Handler<ByteBuf>

object EvoPlusProtocol {

    val Registry = ProtocolRegistry()
    val Handlers = mutableMapOf<String, RawHandler>()

    init {
        scheduleEvery(unit = TimeUnit.SECONDS) {
            if (Connector.isOnDiamondWorld && !Connector.isOnPrisonEvo)
                sendDummy("handshake")
        }

        on<CustomPayloadEvent> {
            if (!channel.startsWith("dw:evoplus")) return@on

            val channel = BufUtil.readString(payload)

            Handlers[channel]?.invoke(payload)

            cancel()
        }
    }

}

//listen
fun onRaw(channel: String, handler: RawHandler) = EvoPlusProtocol.Handlers.put(channel, handler)

inline fun <P : ProtocolSerializable> listen(
    packetType: KClass<P>,
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(packetType.java),
    crossinline handler: Handler<P>
) = onRaw(channel) { handler(BufUtil.readObject(it, packetType.java)) }

inline fun <reified P : ProtocolSerializable> listen(
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(P::class.java),
    crossinline handler: Handler<P>
) = listen(P::class, channel, handler)

//send
inline fun <reified P : ProtocolSerializable> send(
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(P::class.java),
    packet: P
) = sendRaw(channel) { BufUtil.writeObject(this, packet) }

fun sendDummy(channel: String) = sendRaw(channel, Unpooled.buffer())

fun sendRaw(channel: String, block: ByteBuf.() -> Unit) = sendRaw(channel, Unpooled.buffer().apply(block))

fun sendRaw(channel: String, buf: ByteBuf) {
    Client?.networkHandler?.sendPacket(CustomPayloadC2SPacket(UnknownCustomPayload(Identifier.of("dw", channel))))
}