package ru.dargen.evoplus.event.render.entity

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.event.Event

data class RenderPlayerLabelEvent(
    val dispatcher: EntityRenderDispatcher, val matrices: MatrixStack,
    val player: AbstractClientPlayerEntity
) : Event