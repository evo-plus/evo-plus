package ru.dargen.evoplus.protocol.packet

import net.minecraft.network.packet.CustomPayload
import pro.diamondworld.protocol.util.ProtocolSerializable

data class ProtocolPayload<P : ProtocolSerializable>(val payloadId: CustomPayload.Id<ProtocolPayload<P>>, val payload: P) : CustomPayload {

    override fun getId(): CustomPayload.Id<out CustomPayload?>? {
        return payloadId
    }


}
