package ru.dargen.evoplus.event.world

import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket
import ru.dargen.evoplus.event.CancellableEvent

class WorldMapEvent(val id: Int, val packet: MapUpdateS2CPacket) : CancellableEvent() {
}