package ru.dargen.evoplus.features.misc.render

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.Entity
import org.joml.Math
import ru.dargen.evoplus.event.entity.EntityRemoveEvent
import ru.dargen.evoplus.event.entity.EntitySpawnEvent
import ru.dargen.evoplus.event.network.ChangeServerEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.features.misc.RenderFeature
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.state.hbar
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.isNPC
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.progressTo
import java.util.*

object HealthBar {
//
//    private val renderedHealthBars = concurrentHashMapOf<UUID, Node>()
//
//    init {
//        on<ChangeServerEvent> { clearHealthBars() }
//        on<EntitySpawnEvent> {
//            if (!RenderFeature.HealthBarsRender) return@on
//            entity.createHealthBar()
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
//        .forEach { it.createHealthBar() }
//
//    fun clearHealthBars() = renderedHealthBars.values.onEach { WorldContext - it }.clear()
//
//    fun Entity.createHealthBar() {
//        if (this !is AbstractClientPlayerEntity || isNPC || isMainPlayer || isInvisibleTo(Player)) return
//
//        WorldContext + vbox {
//            indent = v3(1.5, 1.5)
//
//            origin = Relative.CenterBottom
//            color = Colors.TransparentBlack
//
//            preTransform { _, tickDelta ->
//                val entityPos = getLerpedPos(tickDelta)
//                position = entityPos.run { v3(x, (y + height + .55) + RenderFeature.HealthBarsY / 10.0, z) }
//                rotation.y = Math.toRadians(Player!!.yaw.toDouble())
//                rotation.x = Math.toRadians(-Player!!.pitch.toDouble())
//            }
//
//            +hbar {
//                translation = v3(z = -.01)
//                size = v3(54.0, TextRenderer.fontHeight * .8)
//                progressRectangle.size.y -= 0.02
//
//                progressRectangle.translation = v3(z = -.01)
//
//                backgroundColor = Colors.Transparent
//
//                val healthText = +text("") {
//                    origin = Relative.Center
//                    align = Relative.Center
//
//                    translation = v3(z = -.02)
//                    scale = v3(.8, .8)
//                }
//
//                tick {
//                    healthText.text = if (RenderFeature.HealthCountRender) "${health.toInt()} HP" else ""
//
//                    progress = (health / maxHealth).toDouble()
//                    animate("color", interpolationTime, interpolationEasing) {
//                        progressRectangle.color = Colors.Green.progressTo(Colors.Red, 1 - progress)
//                    }
//                }
//
//            }
//
//            tick { render = RenderFeature.HealthBarsRender && !isDead && !isSpectator }
//
//            renderedHealthBars[uuid] = this
//        }
//    }

//    private val Color = ColorProgression(Colors.Green, Colors.Red)
//
//    private const val INDENT = 1.5f
//    private const val WIDTH = 54f
//    private const val HEIGHT = 8f
//
//    fun AbstractClientPlayerEntity.renderBar(dispatcher: EntityRenderDispatcher, matrices: MatrixStack) {
//
//        matrices.push {
//            RenderSystem.enableDepthTest()
//
//            translate(0.0F, height + 0.5F, 0.0F)
//            multiply(dispatcher.rotation)
//            translate(0.0F, 0.315F + RenderFeature.HealthBarsY / 10f, 0.0F)
//            normalize3DScale()
//
//            drawRectangle(
//                -(WIDTH / 2f + INDENT), 0f,
//                WIDTH / 2f + INDENT, HEIGHT + INDENT * 2f,
//                zLevel = 0.02f,
//                color = Colors.TransparentBlack
//            )
//            drawRectangle(
//                -(WIDTH / 2f), INDENT,
//                WIDTH / 2f, HEIGHT + INDENT,
//                zLevel = 0.01f,
//                color = Color.at((health / maxHealth).toDouble())
//            )
//            if (RenderFeature.HealthCountRender) {
//                val text = "${health.toInt()} HP"
//                scale(.8f, .8f, .8f)
//                drawWorldText(
//                    text,
//                    -(TextRenderer.getWidth(text) * 0.8f) / 2f,
//                    HEIGHT / 2f + INDENT - (TextRenderer.fontHeight * 0.8f) / 2f + 1
//                )
//            }
//            RenderSystem.disableDepthTest()
//        }
//    }

}