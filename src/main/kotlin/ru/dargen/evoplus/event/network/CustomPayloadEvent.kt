package ru.dargen.evoplus.event.network

import io.netty.buffer.ByteBuf
import ru.dargen.evoplus.event.CancellableEvent

class CustomPayloadEvent(val channel: String, val payload: ByteBuf) : CancellableEvent()