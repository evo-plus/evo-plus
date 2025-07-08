package ru.dargen.evoplus.event.render.base

import net.minecraft.client.render.Frustum
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.CancellableEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class Render3DEvent(val matrixStack: MatrixStack, val frustum: Frustum, renderTickCounter: RenderTickCounter) : CancellableEvent()

