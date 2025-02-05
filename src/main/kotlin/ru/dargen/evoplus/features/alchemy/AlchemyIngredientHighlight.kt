package ru.dargen.evoplus.features.alchemy

import net.minecraft.entity.boss.BossBar
import net.minecraft.particle.ParticleTypes
import org.joml.Vector3f
import ru.dargen.evoplus.feature.render.highligh.ParticleHighlighter
import ru.dargen.evoplus.mixin.render.hud.BossBarHudAccessor
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.render.shape.Box
import java.awt.Color
import kotlin.time.Duration.Companion.seconds

object AlchemyIngredientHighlight : ParticleHighlighter(AlchemyFeature::IngredientHighlight, 2.seconds) {

    override val color = Colors.Red
    override val particles = listOf(ParticleTypes.HAPPY_VILLAGER)

    override fun createHighlight(x: Double, y: Double, z: Double, size: Vector3f, color: Color): Box {
        return super.createHighlight(x, y, z, Vector3f(1.5f), color)
    }

    public override fun shouldProcess() = Client?.inGameHud?.bossBarHud
        ?.cast<BossBarHudAccessor>()
        ?.bossBars?.values
        ?.any { it.color === BossBar.Color.BLUE && it.style === BossBar.Style.PROGRESS } == true

}