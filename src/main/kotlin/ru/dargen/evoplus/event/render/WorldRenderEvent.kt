package ru.dargen.evoplus.event.render

import net.minecraft.client.render.Frustum
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.render.base.Render3DEvent
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class WorldRenderEvent(override val matrixStack: MatrixStack, override val frustum: Frustum, val renderTickCounter: RenderTickCounter) : Render3DEvent(matrixStack, frustum, renderTickCounter)