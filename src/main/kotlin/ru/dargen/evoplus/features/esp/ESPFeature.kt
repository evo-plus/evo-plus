package ru.dargen.evoplus.features.esp

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.value.SwitchColor
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.WorldChunk
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ChunkLoadEvent
import ru.dargen.evoplus.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.event.world.block.BlockChangeEvent
import ru.dargen.evoplus.event.world.block.BlockEntityLoadEvent
import ru.dargen.evoplus.event.world.block.BlockEntityUpdateEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.util.evo.*
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

object ESPFeature : Feature("esp", "Подсветка") {

    private val LuckyBlocks = mutableMapOf<BlockPos, Node>()
    private val Shards = mutableMapOf<BlockPos, Node>()
    private val Barrels = mutableMapOf<BlockPos, Node>()

    var LuckyBlocksEsp = SwitchColor(true, Colors.Red)
    var ShardsEsp = SwitchColor(false, Colors.Deepskyblue)
    var BarrelsEsp = SwitchColor(false, Colors.Green)

    override fun CategoryBuilder.setup() {
        switchColor(::LuckyBlocksEsp, "Подсвечивание лаки-блоков", "Подсвечивает лаки-блоки в шахтах") { state ->
            LuckyBlocks.values.forEach {
                it.enabled = state.enabled
                it.color = state.color
            }
        }

        switchColor(::ShardsEsp, "Подсвечивание осколков", "Подсвечивает золотые и алмазные осколки на шахтах и боссах") { state ->
            Shards.values.forEach {
                it.enabled = state.enabled
                it.color = state.color
            }
        }

        switchColor(::BarrelsEsp, "Подсвечивание бочек", "Подсвечивает бочки в шахтах") { state ->
            Barrels.values.forEach {
                it.enabled = state.enabled
                it.color = state.color
            }
        }
    }

    override fun initialize() {
        on<ChunkLoadEvent>(async = true) {
            chunk.blockEntities.forEach { (blockPos, blockEntity) ->
                recognizeBlock(chunk, blockPos, blockEntity.cachedState)
            }
        }
        on<ChunkUnloadEvent>(async = true) {
            chunk.blockEntityPositions.forEach(this@ESPFeature::tryToRemoveBlock)
        }

        on<BlockChangeEvent> {
            tryToRemoveBlock(blockPos)
            recognizeBlock(chunk, blockPos, newState)
        }
//        if (blockEntities) {
        on<BlockEntityLoadEvent> { tryToRecognizeBlock(chunk, blockEntity) }
        on<BlockEntityUpdateEvent> { tryToRecognizeBlock(chunk, blockEntity) }
//        }

//        on<WorldRenderEvent> {
//            BlockEntities.forEach {
//                val pos = it.pos
//                if (pos in Shards || pos in LuckyBlocks || pos in Barrels) return@forEach
//
//                recognizeBlock(it.world!!.getWorldChunk(pos), pos, it.cachedState)
//            }
//        }
    }

    private fun tryToRecognizeBlock(chunk: WorldChunk, blockEntity: BlockEntity) {
        if (!ShardsEsp.enabled && !LuckyBlocksEsp.enabled && !BarrelsEsp.enabled) return

        val pos = blockEntity.pos
        if (pos in Shards || pos in LuckyBlocks || pos in Barrels) return

        recognizeBlock(chunk, pos, blockEntity.cachedState)
    }

    private fun recognizeBlock(chunk: Chunk, blockPos: BlockPos, blockState: BlockState) {
        if (!ShardsEsp.enabled && !LuckyBlocksEsp.enabled && !BarrelsEsp.enabled) return

        val luckyBlock = blockState.getLuckyBlock(blockPos, chunk)
        val shard = blockState.getShard(blockPos, chunk)
        val barrel = blockState.getBarrel()

        when {
            shard != null -> when {
                blockState.isHead() -> Shards[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderLittleCube(ShardsEsp.color).apply { enabled = ShardsEsp.enabled }

                blockState.isWallHead() -> Shards[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderWallLittleCube(ShardsEsp.color).apply { enabled = ShardsEsp.enabled }
            }

            luckyBlock != null -> when {
                blockState.isHead() -> LuckyBlocks[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderLittleCube(LuckyBlocksEsp.color)
                        .apply { enabled = LuckyBlocksEsp.enabled }

                blockState.isWallHead() -> LuckyBlocks[blockPos.mutableCopy()] =
                    blockPos.mutableCopy().renderWallLittleCube(LuckyBlocksEsp.color)
                        .apply { enabled = LuckyBlocksEsp.enabled }
            }

            barrel != null -> Barrels[blockPos.mutableCopy()] =
                blockPos.mutableCopy().renderCube(BarrelsEsp.color).apply { enabled = BarrelsEsp.enabled }
        }
    }

    fun tryToRemoveBlock(
        blockPos: BlockPos,
        isShard: Boolean = true,
        isLuckyBlock: Boolean = true,
        isBarrel: Boolean = true
    ) {
        if (!ShardsEsp.enabled && !LuckyBlocksEsp.enabled && !BarrelsEsp.enabled) return

        if (isShard) Shards.remove(blockPos)?.let { WorldContext.removeChildren(it) }
        if (isLuckyBlock) LuckyBlocks.remove(blockPos)?.let { WorldContext.removeChildren(it) }
        if (isBarrel) Barrels.remove(blockPos)?.let { WorldContext.removeChildren(it) }
    }

    private fun BlockPos.renderCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + 1, y.toDouble() + 1, z.toDouble())
            this.color = color
            isSeeThrough = true

            size = v3(40.0, 40.0, 40.0)
        }

    private fun BlockPos.renderLittleCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + .75, y.toDouble() + .5, z.toDouble() + 0.25)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }

    private fun BlockPos.renderWallLittleCube(color: Color) =
        WorldContext + cubeOutline {
            position = v3(x.toDouble() + .3, y.toDouble() + .65, z.toDouble() + 0.2)
            this.color = color
            isSeeThrough = true
            size = v3(20.0, 20.0, 20.0)
        }
}