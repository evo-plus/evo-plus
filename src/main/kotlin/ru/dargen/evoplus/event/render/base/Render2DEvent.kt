package ru.dargen.evoplus.event.render.base

import net.minecraft.client.gui.DrawContext
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class Render2DEvent(val context: DrawContext, val tickDelta: Float) : CancellableEvent()