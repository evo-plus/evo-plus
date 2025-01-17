package ru.dargen.evoplus.event.interact

import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.event.CancellableEvent

class BlockBreakEvent(val blockPos: BlockPos) : CancellableEvent()