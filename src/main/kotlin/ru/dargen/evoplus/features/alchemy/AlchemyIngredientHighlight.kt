package ru.dargen.evoplus.features.alchemy

import net.minecraft.entity.boss.BossBar
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ParticleEvent
import ru.dargen.evoplus.mixin.render.hud.BossBarHudAccessor
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.animation.Easings
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.minusAssign
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.newCacheExpireAfterAccess
import kotlin.collections.contains
import kotlin.time.Duration.Companion.seconds

object AlchemyIngredientHighlight {

    private val spots = newCacheExpireAfterAccess<Long, CubeOutlineNode>(1.seconds) { _, node -> node.hide() }
    private val Types =
        listOf(ParticleTypes.HAPPY_VILLAGER)

    init {
        on<ParticleEvent> {
            if (AlchemyFeature.IngredientHighlight
                && packet.parameters.type in Types
                && isAlchemyIngredientFields()
            ) with(packet) {
                spots.asMap().getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
                    WorldContext + highlight(x, y, z, 1.0).apply { show() }
                }
            }
        }
    }

    private fun isAlchemyIngredientFields() = Client?.inGameHud?.bossBarHud
        ?.cast<BossBarHudAccessor>()
        ?.bossBars?.values
        ?.any { it.color === BossBar.Color.BLUE && it.style === BossBar.Style.PROGRESS } == true

    private fun highlight(x: Double, y: Double, z: Double, size: Double) = cubeOutline {
        position = v3(x, y + .5, z)
        this.size = v3(size * 80, size * 80, size * 80)
        origin = v3(.5, .5, .5)
        isSeeThrough = true

        color = Colors.Red
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