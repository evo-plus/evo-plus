package ru.dargen.evoplus.render.context

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.VertexBuffer
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.render.WorldRenderEvent
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.render.BufferBuilderStorage
import ru.dargen.evoplus.util.render.Camera

data object WorldContext : RenderContext() {

    override var scale = v3(-.025, -.025, .025)
    override var translationScale = v3(1.0, 1.0, 1.0) / scale

    override fun registerRenderHandlers() {
        on<WorldRenderEvent> {
            Camera = camera
            BufferBuilderStorage = bufferBuilderStorage

            context.matrices.push()
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableCull()

            camera.pos.run { context.matrices.translate(-x, -y, -z) }

            render(context, tickDelta)

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
            VertexBuffer.unbind()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
            context.matrices.pop()
        }
    }

    override fun allowInput() = Client?.currentScreen == null

}
