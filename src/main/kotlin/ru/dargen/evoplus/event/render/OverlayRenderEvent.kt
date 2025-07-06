package ru.dargen.evoplus.event.render

import net.minecraft.client.gui.DrawContext
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class OverlayRenderEvent(context: DrawContext, tickDelta: Float) : RenderEvent(context, tickDelta) {}