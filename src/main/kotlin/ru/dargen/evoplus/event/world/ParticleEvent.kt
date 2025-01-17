package ru.dargen.evoplus.event.world

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import ru.dargen.evoplus.event.CancellableEvent

class ParticleEvent(val packet: ParticleS2CPacket) : CancellableEvent() {
}