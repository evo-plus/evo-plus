package ru.dargen.evoplus.features.dungeon

import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.minusAssign
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customModelData
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

object DungeonDecorationHighlight {

    private val chests = newCacheExpireAfterAccess<Long, CubeOutlineNode>(1.seconds) { _, node -> node.hide() }
    private val ChestTypes = (10271..10282) + (10311..10327)

    init {
        scheduleEvery(500, 500, unit = TimeUnit.MILLISECONDS) {
            if (DungeonFeature.DecorationHighlight && PlayerDataCollector.location.run { isDungeon }) {
                WorldEntities.asSequence()
                    .filterIsInstance<DisplayEntity.ItemDisplayEntity>()
                    .filter { it.isChest() }
                    .forEach {
                        chests.asMap().getOrPut(BlockPos.asLong(it.x.toInt(), it.y.toInt(), it.z.toInt())) {
                            WorldContext + chestHighlight(it.x, it.y, it.z).apply { show() }
                        }
                    }
            }
        }
    }

    private fun DisplayEntity.ItemDisplayEntity.isChest() =
        itemStack?.run { item === Items.PAPER && customModelData?.let(ChestTypes::contains) == true } == true

    private fun chestHighlight(x: Double, y: Double, z: Double, size: Double = 1.05) = cubeOutline {
        position = v3(x, y + .5, z)
        scaledSize = v3(size, size, size)
        origin = v3(.5, .0, .5)
        isSeeThrough = true
        width = 3.0

        color = Colors.Yellow
        scale = v3()
    }

    private fun CubeOutlineNode.show() = animate("fade", .2, Easings.BackOut) {
        scale = v3(1.0, 1.0, 1.0)
    }

    private fun CubeOutlineNode.hide() = animate("fade", .2) {
        scale = v3()
        after { WorldContext -= this@hide }
    }

}