package ru.dargen.evoplus.event.resourcepack

import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket
import ru.dargen.evoplus.event.CancellableEvent

class ResourcePackRequestEvent(val request: ResourcePackSendS2CPacket, var responseAccepted: Boolean = false) : CancellableEvent() {
}