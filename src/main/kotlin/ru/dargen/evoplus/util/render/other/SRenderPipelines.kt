package ru.dargen.evoplus.util.render.other

import gg.essential.universal.UGraphics
import gg.essential.universal.render.URenderPipeline
import gg.essential.universal.shader.BlendState

object SRenderPipelines {

    private val translucentBlendState = BlendState(
        BlendState.Equation.ADD,
        BlendState.Param.SRC_ALPHA,
        BlendState.Param.ONE_MINUS_SRC_ALPHA,
        BlendState.Param.ONE
    )

    val guiPipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/gui",
        UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR
    ).apply {
        blendState = translucentBlendState
        depthTest = URenderPipeline.DepthTest.LessOrEqual
    }.build()

    val guiTexturePipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/gui_texture",
        UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR
    ).apply {
        blendState = translucentBlendState
        depthTest = URenderPipeline.DepthTest.LessOrEqual
    }.build()

    val linesPipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/lines",
        UGraphics.DrawMode.LINES, UGraphics.CommonVertexFormats.POSITION_COLOR
    ).apply {
        blendState = BlendState.ALPHA
    }.build()

    val noDepthLinesPipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/no_depth_lines",
        UGraphics.DrawMode.LINES, UGraphics.CommonVertexFormats.POSITION_COLOR
    ).apply {
        depthTest = URenderPipeline.DepthTest.Always
        blendState = BlendState.ALPHA
        culling = false
    }.build()

    val boxPipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/box",
        UGraphics.DrawMode.TRIANGLE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR
    ).apply {
        blendState = BlendState.ALPHA
    }.build()

    val noDepthBoxPipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/no_depth_box",
        UGraphics.DrawMode.TRIANGLE_STRIP, UGraphics.CommonVertexFormats.POSITION_COLOR
    ).apply {
        depthTest = URenderPipeline.DepthTest.Always
        blendState = BlendState.ALPHA
        culling = false
    }.build()

    val vignettePipeline = URenderPipeline.builderWithDefaultShader("evoplus:pipeline/vignette",
        UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR
    ).apply {
        blendState = BlendState(BlendState.Equation.ADD, BlendState.Param.ZERO, BlendState.Param.ONE_MINUS_SRC_COLOR)
        depthTest = URenderPipeline.DepthTest.Always
        depthMask = false
    }.build()

}
