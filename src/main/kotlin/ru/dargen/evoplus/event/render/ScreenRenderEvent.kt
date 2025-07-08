package ru.dargen.evoplus.event.render

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import ru.dargen.evoplus.event.render.base.Render2DEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class ScreenRenderEvent(val screen: Screen, context: DrawContext, tickDelta: Float) : Render2DEvent(context, tickDelta) {

    @KotlinOpens
    class Pre(screen: Screen, context: DrawContext, tickDelta: Float) : ScreenRenderEvent(screen, context, tickDelta)
    @KotlinOpens
    class Post(screen: Screen, context: DrawContext, tickDelta: Float) : ScreenRenderEvent(screen, context, tickDelta)

}