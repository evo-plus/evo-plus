package ru.dargen.evoplus.event.world.block

import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.event.CancellableEvent

class BlockEntityUnloadEvent(val chunk: WorldChunk, val pos: BlockPos) : CancellableEvent() {

    val blockEntity: BlockEntity? by lazy { chunk.getBlockEntity(pos) }

}