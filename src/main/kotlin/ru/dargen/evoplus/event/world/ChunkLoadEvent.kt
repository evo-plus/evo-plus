package ru.dargen.evoplus.event.world

import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.event.Event

class ChunkLoadEvent(val chunk: WorldChunk) : Event

class ChunkUnloadEvent(val chunk: WorldChunk) : Event