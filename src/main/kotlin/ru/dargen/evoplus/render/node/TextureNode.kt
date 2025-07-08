package ru.dargen.evoplus.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import java.util.function.Function

@KotlinOpens
class TextureNode : Node() {

    lateinit var identifier: Identifier
    lateinit var renderLayer: RenderLayer
    var texture: Int = -1

    var textureOffset by proxied(v3())
    var textureSize by proxied(v3(256.0, 256.0))
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

        val renderLayers: Function<Identifier, RenderLayer> = if (this::renderLayer.isInitialized)
            Function { _ -> renderLayer }
        else Function { id -> RenderLayer.getGui() }

        context.drawTexture(
            renderLayers,
            identifier,
            0, 0,
            textureOffset.x.toFloat(), textureOffset.y.toFloat(),
            size.x.toInt(), size.y.toInt(),
            textureSize.x.toInt(), textureSize.y.toInt(), color.rgb
        )
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

fun texture(identifier: Identifier, renderLayer: RenderLayer, block: TextureNode.() -> Unit = {}) = texture {
    this.identifier = identifier
    this.renderLayer = renderLayer
    block()
}