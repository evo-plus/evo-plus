//package ru.dargen.evoplus.features.esp
//
//import dev.evoplus.feature.setting.Settings
//import net.minecraft.block.BlockState
//import net.minecraft.command.argument.BlockPosArgumentType.blockPos
//import net.minecraft.item.Item
//import ru.dargen.evoplus.event.on
//import ru.dargen.evoplus.event.world.ChunkLoadEvent
//import ru.dargen.evoplus.event.world.ChunkUnloadEvent
//import ru.dargen.evoplus.event.world.block.BlockChangeEvent
//import ru.dargen.evoplus.event.world.block.BlockEntityLoadEvent
//import ru.dargen.evoplus.event.world.block.BlockEntityUpdateEvent
//import ru.dargen.evoplus.feature.Feature
//import ru.dargen.evoplus.features.esp.ESPFeature
//import ru.dargen.evoplus.features.esp.ESPFeature.recognizeBlock
//import ru.dargen.evoplus.features.esp.ESPFeature.tryToRecognizeBlock
//import ru.dargen.evoplus.features.esp.ESPFeature.tryToRemoveBlock
//import ru.dargen.evoplus.scheduler.async
//import ru.dargen.evoplus.util.kotlin.KotlinOpens
//import java.awt.Color
//import kotlin.collections.component1
//import kotlin.collections.component2
//
//@KotlinOpens
//class ESPFeatureCategory(
//    id: String, name: String,
//    val blockEntities: Boolean,
//    val predicate: (BlockState) -> Boolean, val items: List<Item>,
//) : Feature(id, name) {
//
//    private var enabled = true
//
//    override fun Settings.CategoryBuilder.setup() {
//        switch(::enabled, name)
//        items.forEach { color(it::color, it.displayName, "Цвет подсветки") }
//    }
//
//    override fun initialize() {
//        on<ChunkLoadEvent>(async = true) {
//            chunk.blockEntities.forEach { (blockPos, blockEntity) ->
//                recognizeBlock(chunk, blockPos, blockEntity.cachedState)
//            }
//        }
//        on<ChunkUnloadEvent>(async = true) {
//            chunk.blockEntityPositions.forEach(this@ESPFeature::tryToRemoveBlock)
//        }
//
//        on<BlockChangeEvent> {
//            tryToRemoveBlock(blockPos)
//            recognizeBlock(chunk, blockPos, newState)
//        }
//        if (blockEntities) {
//            on<BlockEntityLoadEvent> { tryToRecognizeBlock(chunk, blockEntity) }
//            on<BlockEntityUpdateEvent> { tryToRecognizeBlock(chunk, blockEntity) }
//        }
//    }
//
//    interface Item {
//
//        var color: Color
//        val displayName: String
//
//        fun isThis(state: BlockState): Boolean
//
//    }
//
//}