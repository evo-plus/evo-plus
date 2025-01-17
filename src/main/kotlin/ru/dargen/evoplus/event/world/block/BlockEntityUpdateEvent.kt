package ru.dargen.evoplus.event.world.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.event.CancellableEvent

class BlockEntityUpdateEvent(val chunk: WorldChunk, val blockEntity: BlockEntity) : CancellableEvent()