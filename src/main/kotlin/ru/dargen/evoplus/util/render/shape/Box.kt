package ru.dargen.evoplus.util.render.shape

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.util.math.MatrixStack
import org.joml.Vector3f
import ru.dargen.evoplus.util.render.drawBoxOutline
import ru.dargen.evoplus.util.render.push
import java.awt.Color

data class Box(
    var position: Vector3f,
    var size: Vector3f, var color: Color,
    var centered: Boolean = false,
) {

    fun draw(matrices: MatrixStack) {
        matrices.push {
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableCull()
            RenderSystem.disableDepthTest()
            if (centered) translate(position.x - size.x / 2, position.y - size.y / 2, position.z - size.z / 2)
            else translate(position.x, position.y, position.z)

            matrices.drawBoxOutline(0f, 0f, 0f, size.x, size.y, size.z, color)
            RenderSystem.enableDepthTest()
        }
    }

}