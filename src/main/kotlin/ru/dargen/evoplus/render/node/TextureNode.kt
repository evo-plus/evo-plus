package ru.dargen.evoplus.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3

@KotlinOpens
class TextureNode : Node() {

    lateinit var identifier: Identifier
    var texture: Int = -1

    var textureOffset by proxied(v3())
    var textureSize by proxied(v3(256.0, 256.0))
    var repeating = false
    var blend = true

    init {
        size = textureSize.clone()
    }

    override fun renderElement(context: DrawContext, tickDelta: Float) {
        if (!this::identifier.isInitialized) {
            if (texture == -1) return
            RenderSystem.setShaderTexture(0, texture)
        } else RenderSystem.setShaderTexture(0, identifier)

        if (blend) RenderSystem.enableBlend()

//        if (repeating) DrawContextExtensions.drawRepeatingTexture(
//            matrices, 0, 0,
//            size.x.toInt(), size.y.toInt(),
//            textureOffset.x.toInt(), textureOffset.y.toInt(),
//            textureSize.x.toInt(), textureSize.y.toInt()
//        ) else DrawableHelper.drawTexture(
//            matrices, 0, 0,
//            textureOffset.x.toFloat(), textureOffset.y.toFloat(),
//            size.x.toInt(), size.y.toInt(),
//            textureSize.x.toInt(), textureSize.y.toInt()
//        )
    }

}

fun texture(block: TextureNode.() -> Unit = {}) = TextureNode().apply(block)

fun texture(identifier: Identifier, block: TextureNode.() -> Unit = {}) = texture {
    this.identifier = identifier
    block()
}

fun texture(texture: Int, block: TextureNode.() -> Unit = {}) = texture {
    this.texture = texture
    block()
}