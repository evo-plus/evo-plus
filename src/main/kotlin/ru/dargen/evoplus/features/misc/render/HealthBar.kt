package ru.dargen.evoplus.features.misc.render

object HealthBar {

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

}

//    init {
//
//        on<RenderPlayerLabelEvent> {
//            if (RenderFeature.HealthBarsRender) player.renderBar(dispatcher, context)
//        }
//
//    }
//
//    private val Color = ColorProgression(Colors.Green, Colors.Red)
//
//    private const val INDENT = 1.5f
//    private const val WIDTH = 54f
//    private const val HEIGHT = 8f
//
//    fun AbstractClientPlayerEntity.renderBar(dispatcher: EntityRenderDispatcher, context: DrawContext) {
//
//        context.matrices.push {
//            RenderSystem.enableDepthTest()
//
//            translate(0.0F, height + 0.5F, 0.0F)
//            multiply(dispatcher.rotation)
//            translate(0.0F, 0.315F + RenderFeature.HealthBarsY / 10f, 0.0F)
//            normalize3DScale()
//
//            context.drawRectangle(
//                -(WIDTH / 2f + INDENT), 0f,
//                WIDTH / 2f + INDENT, HEIGHT + INDENT * 2f,
//                zLevel = 0.02f,
//                color = Colors.TransparentBlack
//            )
//            context.drawRectangle(
//                -(WIDTH / 2f), INDENT,
//                WIDTH / 2f, HEIGHT + INDENT,
//                zLevel = 0.01f,
//                color = Color.at((health / maxHealth).toDouble())
//            )
//            if (RenderFeature.HealthCountRender) {
//                val text = "${health.toInt()} HP"
//                scale(.8f, .8f, .8f)
//                context.drawWorldText(
//                    text,
//                    -(TextRenderer.getWidth(text) * 0.8f) / 2f,
//                    HEIGHT / 2f + INDENT - (TextRenderer.fontHeight * 0.8f) / 2f + 1
//                )
//            }
//            RenderSystem.disableDepthTest()
//        }
//    }
//
//}
