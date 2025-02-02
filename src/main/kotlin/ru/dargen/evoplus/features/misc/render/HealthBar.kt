package ru.dargen.evoplus.features.misc.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.entity.RenderPlayerLabelEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.util.render.ColorProgression
import ru.dargen.evoplus.util.render.TextRenderer
import ru.dargen.evoplus.util.render.drawRectangle
import ru.dargen.evoplus.util.render.drawWorldText
import ru.dargen.evoplus.util.render.normalize3DScale
import ru.dargen.evoplus.util.render.push

//TODO: maybe remove for npcs
object HealthBar : Feature(name = "Полоса здоровья") {

    private var enabled = true
    private var offset = 0f
    private var showHealth = true

    override fun CategoryBuilder.setup() {
        switch(
            ::enabled,
            "Отображение",
            "Показывает полоску здоровья над игроками"
        )
        decimal(
            ::offset,
            "Сдвиг полоски",
            "Регулирует высоту полоски здоровья над игроком",
            range = 0f..5f
        )
        switch(
            ::showHealth,
            "Здоровье игрока",
            "Показывает числовое значение здоровья над игроком"
        )
    }

    override fun initialize() {
        on<RenderPlayerLabelEvent> {
            if (enabled) player.renderBar(dispatcher, matrices)
        }
    }

    private var Color = ColorProgression(Colors.Green, Colors.Red)

    //    private var SneakColor = Color.map(java.awt.Color::darker).map { it.withAlpha(63) }
    private const val INDENT = 1.5f
    private const val WIDTH = 54f
    private const val HEIGHT = 8f

    private fun AbstractClientPlayerEntity.renderBar(dispatcher: EntityRenderDispatcher, matrices: MatrixStack) {
        matrices.push {
            RenderSystem.enableDepthTest()

            translate(0.0F, height + 0.5F, 0.0F)
            multiply(dispatcher.rotation)
            translate(0.0F, 0.315F + offset / 10f, 0.0F)
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
                color = /*(if (isSneaky) SneakColor else Color)*/Color.at((health / maxHealth).toDouble())
            )
            if (showHealth) {
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
