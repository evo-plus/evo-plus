package ru.dargen.evoplus.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.util.Identifier
import ru.dargen.evoplus.EvoPlus

fun Identifier.bindTexture() {
    RenderSystem.setShaderTexture(0, this)
}