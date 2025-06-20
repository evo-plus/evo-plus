package ru.dargen.evoplus.features.misc.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.features.misc.RenderFeature
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.render.*

object HealthBar {

//    private val renderedHealthBars = concurrentHashMapOf<UUID, Node>()
//
//    init {
//        on<ChangeServerEvent> { clearHealthBars() }
//        on<EntitySpawnEvent> {
//            if (!RenderFeature.HealthBarsRender) return@on
//            entity.renderBar()
//        }
//        on<EntityRemoveEvent> {
//            if (!RenderFeature.HealthBarsRender) return@on
//            renderedHealthBars.remove(entity.uuid)?.let { WorldContext - it }
//        }
//    }
//
//    fun updateRender(state: Boolean) {
//        if (state) fillHealthBars()
//        else clearHealthBars()
//    }
//
//    fun fillHealthBars() = WorldEntities
//        .filterIsInstance<AbstractClientPlayerEntity>()
//        .forEach { it.renderBar() }
//
//    fun clearHealthBars() = renderedHealthBars.values.onEach { WorldContext - it }.clear()

    private val Color = ColorProgression(Colors.Green, Colors.Red)

    private const val INDENT = 1.5f
    private const val WIDTH = 54f
    private const val HEIGHT = 8f

    fun AbstractClientPlayerEntity.renderBar(dispatcher: EntityRenderDispatcher, matrices: MatrixStack) {

        matrices.push {
            RenderSystem.enableDepthTest()

            translate(0.0F, height + 0.5F, 0.0F)
            multiply(dispatcher.rotation)
            translate(0.0F, 0.315F + RenderFeature.HealthBarsY / 10f, 0.0F)
            normalize3DScale()

            drawRectangle(
                -(WIDTH / 2f + INDENT), 0f,
                WIDTH / 2f + INDENT, HEIGHT + INDENT * 2f,
                zLevel = 0.02f,
                color = Colors.TransparentBlack
            )
            drawRectangle(
                -(WIDTH / 2f), INDENT,
                WIDTH / 2f, HEIGHT + INDENT,
                zLevel = 0.01f,
                color = Color.at((health / maxHealth).toDouble())
            )
            if (RenderFeature.HealthCountRender) {
                val text = "${health.toInt()} HP"
                scale(.8f, .8f, .8f)
                drawWorldText(
                    text,
                    -(TextRenderer.getWidth(text) * 0.8f) / 2f,
                    HEIGHT / 2f + INDENT - (TextRenderer.fontHeight * 0.8f) / 2f + 1
                )
            }
            RenderSystem.disableDepthTest()
        }
    }

}