package ru.dargen.evoplus.protocol

import io.netty.buffer.ByteBuf
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.UnknownCustomPayload
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import pro.diamondworld.protocol.ProtocolRegistry
import pro.diamondworld.protocol.util.BufUtil
import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.protocol.packet.DummyPayload
import ru.dargen.evoplus.protocol.packet.ProtocolPayload
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
            if (Connector.isOnDiamondWorld && !Connector.isOnPrisonEvo) {
                Client?.networkHandler?.sendPacket(CustomPayloadC2SPacket(UnknownCustomPayload(DummyPayload.INSTANCE.id?.id)))
            }
        }

//        on<CustomPayloadEvent> {
//            if (!channel.startsWith("dw")) return@on
//            val channel = channel.drop(3)
//
//            Handlers[channel]?.invoke(payload)
//
//            cancel()
//        }
    }

    fun <P : ProtocolSerializable> registerHandler(id: String, payloadClass: Class<P>, handler: Handler<P>) {
        val id = CustomPayload.Id<ProtocolPayload<P>>(Identifier.of("dw", id))

        PayloadTypeRegistry.playC2S().register(
            id,
            PacketCodec.ofStatic(
                { buf, v -> v.payload.write(buf) },
                { ProtocolPayload(id, BufUtil.readObject(it, payloadClass)) }
            )
        )
        ServerPlayNetworking.registerGlobalReceiver(id) { payload, context ->
            println(payload.id)
            handler(payload.payload)
        }
    }

}

//listen
fun onRaw(channel: String, handler: RawHandler) = EvoPlusProtocol.Handlers.put(channel, handler)

inline fun <P : ProtocolSerializable> listen(
    packetType: KClass<P>,
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(packetType.java),
    noinline handler: Handler<P>
) = EvoPlusProtocol.registerHandler(channel, packetType.java, handler)

inline fun <reified P : ProtocolSerializable> listen(
    channel: String = EvoPlusProtocol.Registry.lookupOrRegisterChannel(P::class.java),
    noinline handler: Handler<P>
) = listen(P::class, channel, handler)
