package ru.dargen.evoplus.protocol.packet

import io.netty.buffer.ByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import pro.diamondworld.protocol.util.ProtocolSerializable

object DummyPayload : ProtocolSerializable {

    override fun read(buf: ByteBuf?) {}
    override fun write(buf: ByteBuf?) {}

    val INSTANCE = ProtocolPayload(CustomPayload.Id(Identifier.of("dw:evoplus")), this)

}