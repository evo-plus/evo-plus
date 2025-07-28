package ru.dargen.evoplus.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.RenderContext
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.util.collection.anyOfAll
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Window
import ru.dargen.evoplus.util.render.DefaultScale
import ru.dargen.evoplus.util.render.rotate
import ru.dargen.evoplus.util.render.scale
import ru.dargen.evoplus.util.render.translate
import java.awt.Color

typealias RenderHandler<N> = N.(context: DrawContext, tickDelta: Float) -> Unit

typealias KeyHandler<N> = N.(key: Int, state: Boolean) -> Boolean
typealias CharHandler<N> = N.(char: Char, code: Int) -> Boolean

typealias MouseWheelHandler<N> = N.(mouse: Vector3, verticalWheel: Double, horizontalWheel: Double) -> Boolean
typealias MouseClickHandler<N> = N.(mouse: Vector3, button: Int, state: Boolean) -> Boolean
typealias MouseMoveHandler<N> = N.(mouse: Vector3) -> Boolean

typealias ResizeHandler<N> = N.() -> Unit
typealias HoverHandler<N> = N.(mouse: Vector3, state: Boolean) -> Unit
typealias TickHandler<N> = N.() -> Unit

//TODO: make interactions with states pattern && default supplied as true
@KotlinOpens
abstract class Node {

    //position
    var position by proxied(Vector3())
    var translation by proxied(Vector3())
    var origin by proxied(Relative.LeftTop)
    var align by proxied(Relative.LeftTop)

    //visual properties
    var size by proxied(Vector3())
    var scale by proxied(Vector3(1.0))
    var rotation by proxied(Vector3())

    var color by proxied<Color>(Colors.Transparent)

    //node properties
    var _childrens = mutableListOf<Node>()
        set(value) {
            value.forEach {
                if (it.parent !== this) it.parent?.removeChildren(it)

                it.resize()

                it.parent = this
            }
            field = value
        }
    val children get() = _childrens.toList()

    val enabledChildren get() = children.asSequence().filter(Node::enabled)
    var parent: Node? = null

    var scissorIndent by proxied(Vector3())
    var isSeeThrough = false
    var isScissor = false
    var enabled = true
    var render = true
    var isHovered = false

    val preTransformHandlers = mutableSetOf<RenderHandler<Node>>()
    val postTransformHandlers = mutableSetOf<RenderHandler<Node>>()
    val preRenderHandlers = mutableSetOf<RenderHandler<Node>>()
    val postRenderHandlers = mutableSetOf<RenderHandler<Node>>()

    val clickHandlers = mutableSetOf<MouseClickHandler<Node>>()
    val wheelHandlers = mutableSetOf<MouseWheelHandler<Node>>()
    val moveHandlers = mutableSetOf<MouseMoveHandler<Node>>()

    val keyHandlers = mutableSetOf<KeyHandler<Node>>()
    val charHandlers = mutableSetOf<CharHandler<Node>>()

    val resizeHandlers = mutableSetOf<ResizeHandler<Node>>()
    val hoverHandlers = mutableSetOf<HoverHandler<Node>>()
    val asyncTickHandlers = mutableSetOf<TickHandler<Node>>()
    val preTickHandlers = mutableSetOf<TickHandler<Node>>()
    val postTickHandlers = mutableSetOf<TickHandler<Node>>()

    val wholePosition: Vector3
        get() {

            val wholeScale = wholeScale
            return (parent?.size?.times(align) ?: Vector3.Mutable())
                .plus(translation).plus(position)
                .times(wholeScale.x / scale.x, wholeScale.y / scale.y, wholeScale.z / scale.z)
                .plus(wholeScale.times(!size).times(origin))
                .plus(parent?._wholePosition() ?: Vector3.Zero)
        }
    val wholeScale get() = (parent?._wholeScale() ?: Vector3.Mutable(1.0)) * scale
    val wholeRotation get() = (parent?.rotation ?: Vector3.Mutable()) + rotation
    val wholeSize get() = wholeScale * size

    val isWorldElement get() = this@Node.renderContext is WorldContext
    val renderContext: RenderContext? get() = parent?.renderContext ?: safeCast<RenderContext>()

    //dispatchers
    fun asyncTick() {
        asyncTickHandlers.forEach { it() }
        children.forEach { it.asyncTick() }
    }

    fun preTick() {
        preTickHandlers.forEach { it() }
        children.forEach { it.preTick() }
    }

    fun postTick() {
        postTickHandlers.forEach { it() }
        children.forEach { it.postTick() }
    }

    fun resize() {
        resizeHandlers.forEach { it() }
        children.forEach { it.resize() }
    }

    fun mouseMove(mouse: Vector3): Boolean {
        if (!enabled) return false

        if (children.any { it.mouseMove(mouse) }) {
            return true
        }

        updateHover(mouse)
        return moveHandlers.anyOfAll { it(mouse) }
    }

    fun mouseClick(mouse: Vector3, button: Int, state: Boolean): Boolean {
        if (!enabled) return false

        if (children.any { it.mouseClick(mouse, button, state) }) {
            return true
        }

        return clickHandlers.anyOfAll { it(mouse, button, state) }
    }

    fun mouseWheel(mouse: Vector3, verticalWheel: Double, horizontalWheel: Double): Boolean {
        if (!enabled) return false

        if (children.any { it.mouseWheel(mouse, verticalWheel, horizontalWheel) }) {
            return true
        }

        return wheelHandlers.anyOfAll { it(mouse, verticalWheel, horizontalWheel) }
    }

    fun changeKey(key: Int, state: Boolean): Boolean {
        if (!enabled) return false

        if (children.any { it.changeKey(key, state) }) {
            return true
        }

        return keyHandlers.anyOfAll { it(key, state) }
    }

    fun typeChar(char: Char, code: Int): Boolean {
        if (!enabled) return false

        if (children.any { it.typeChar(char, code) }) {
            return true
        }

        return charHandlers.anyOfAll { it(char, code) }
    }


    fun updateHover(mouse: Vector3) {
        val positionStart = wholePosition.apply { z = .0 }
        val positionEnd = (positionStart + size * wholeScale).apply { z = .0 }

        val hovered = mouse.isBetween(positionStart, positionEnd)

        if (hovered == isHovered) return

        isHovered = hovered
        hoverHandlers.forEach { it(mouse, hovered) }

        children.forEach { it.updateHover(mouse) }
    }

    fun render(context: DrawContext, tickDelta: Float) {
        if (!enabled || !render) return
        context.matrices.push()

        preTransformHandlers.forEach { it(context, tickDelta) }

        parent?.let { context.matrices.translate(it.size, align) }

        val positionScale = parent?.safeCast<RenderContext>()?.translationScale ?: DefaultScale
        context.matrices.translate(translation, positionScale)
        context.matrices.translate(position, positionScale)

        context.matrices.scale(scale)

        context.matrices.rotate(rotation)

        context.matrices.translate(size, origin, -1.0)

        preRenderHandlers.forEach { it(context, tickDelta) }

        renderElement(context, tickDelta)

        if (isScissor) {
            val position = (wholePosition + scissorIndent * wholeScale) * Overlay.ScaleFactor
            val size = (wholeSize - scissorIndent * 2.0 * wholeScale) * Overlay.ScaleFactor

            RenderSystem.enableScissor(
                position.x.toInt(), (Window.framebufferHeight - position.y - size.y.toInt()).toInt(),
                size.x.toInt(), size.y.toInt()
            )
        }

        children.forEach { it.render(context, tickDelta) }

        postRenderHandlers.forEach { it(context, tickDelta) }

        if (isScissor)
            RenderSystem.disableScissor()

        postTransformHandlers.forEach { it(context, tickDelta) }
        context.matrices.pop()
    }

    fun renderElement(context: DrawContext, tickDelta: Float) {}
    fun renderBox(matrices: MatrixStack) {}

    //children
    fun addChildren(children: Collection<Node>) {
        children.forEach { child ->
            if (child.parent !== this) child.parent?.removeChildren(child)
            child.resize()

            child.parent = this
            this._childrens.add(child)
        }
    }

    fun addChildren(vararg children: Node) = addChildren(children.toList())

    fun removeChildren(children: Collection<Node>) {
        children.forEach {
            it.parent = null
            it.resize()
            this._childrens.remove(it)
        }
    }

    fun removeChildren(vararg children: Node) =
        removeChildren(children.toList())

    operator fun <N : Node> N.unaryPlus() = apply { this@Node.addChildren(this) }

    operator fun <N : Node> N.unaryMinus() = apply { this@Node.removeChildren(this) }
}

//hover
infix fun <N : Node> N.hover(handler: HoverHandler<N>) = apply { hoverHandlers.add(handler.cast()) }

infix fun <N : Node> N.hoverIn(handler: N.(mouse: Vector3) -> Unit) =
    hover { mouse, state -> if (state) handler(mouse) }

infix fun <N : Node> N.hoverOut(handler: N.(mouse: Vector3) -> Unit) =
    hover { mouse, state -> if (!state) handler(mouse) }

//wheel
infix fun <N : Node> N.wheel(handler: MouseWheelHandler<N>) =
    apply { wheelHandlers.add(handler.cast()) }

infix fun <N : Node> N.hWheel(handler: N.(mouse: Vector3, wheel: Double) -> Boolean) =
    wheel { mouse, _, horizontalWheel -> handler(mouse, horizontalWheel) }

infix fun <N : Node> N.vWheel(handler: N.(mouse: Vector3, wheel: Double) -> Boolean) =
    wheel { mouse, verticalWheel, _ -> handler(mouse, verticalWheel) }

//click
infix fun <N : Node> N.click(handler: MouseClickHandler<N>) =
    apply { clickHandlers.add(handler.cast()) }

fun <N : Node> N.click(_button: Int, handler: N.(mouse: Vector3, state: Boolean) -> Boolean) =
    click { mouse, button, state -> if (button == _button) handler(this, mouse, state) else false }

infix fun <N : Node> N.rightClick(handler: N.(mouse: Vector3, state: Boolean) -> Boolean) =
    click(1, handler)

infix fun <N : Node> N.leftClick(handler: N.(mouse: Vector3, state: Boolean) -> Boolean) =
    click(0, handler)

//move
fun <N : Node> N.drag(
    _button: Int? = null,
    inOutHandler: N.(dragged: Boolean) -> Unit = {},
    handler: N.(startPosition: Vector3, delta: Vector3) -> Unit = { _, _ -> },
) = apply {
    var startPosition = Vector3()
    var dragged = false

    click { mouse, button, state ->
        if (_button != null && button != _button) return@click false

        if (isHovered && state) {
            dragged = true
            inOutHandler(dragged)
            handler(startPosition, 0.v3)
            startPosition = mouse.clone()
            return@click true
        } else if (dragged && !state) {
            dragged = false
            inOutHandler(dragged)
        }

        return@click false
    }
    mouseMove {
        if (dragged) {
            handler(startPosition, it - startPosition)
            return@mouseMove true
        }

        return@mouseMove false
    }
}

infix fun <N : Node> N.mouseMove(handler: MouseMoveHandler<N>) =
    apply { moveHandlers.add(handler.cast()) }

//type
infix fun <N : Node> N.key(handler: KeyHandler<N>) = apply { keyHandlers.add(handler.cast()) }

fun <N : Node> N.key(_key: Int, handler: N.(state: Boolean) -> Boolean) =
    key { key, state -> if (key == _key) handler(this, state) else false }

infix fun <N : Node> N.releaseKey(handler: N.(key: Int) -> Boolean) =
    key { key, state -> if (!state) handler(key) else false }

fun <N : Node> N.releaseKey(_key: Int, handler: N.() -> Boolean) =
    releaseKey { if (it == _key) handler() else false }

infix fun <N : Node> N.typeKey(handler: N.(key: Int) -> Boolean) =
    key { key, state -> if (state) handler(key) else false }

fun <N : Node> N.typeKey(_key: Int, handler: N.() -> Boolean) =
    typeKey { if (it == _key) handler() else false }

infix fun <N : Node> N.type(handler: CharHandler<N>) =
    apply { charHandlers.add(handler.cast()) }

fun <N : Node> N.type(_char: Char, handler: N.() -> Boolean) =
    type { char, _ -> if (char == _char) handler() else false }

//tick
infix fun <N : Node> N.preTick(handler: TickHandler<N>) = apply { preTickHandlers.add(handler.cast()) }

infix fun <N : Node> N.postTick(handler: TickHandler<N>) =
    apply { postTickHandlers.add(handler.cast()) }

infix fun <N : Node> N.asyncTick(handler: TickHandler<N>) = apply { asyncTickHandlers.add(handler.cast()) }

fun <N : Node> N.tick(async: Boolean = false, handler: TickHandler<N>) =
    if (async) asyncTick(handler) else postTick(handler)

infix fun <N : Node> N.resize(handler: ResizeHandler<N>) = apply { resizeHandlers.add(handler.cast()) }

infix fun <N : Node> N.preTransform(handler: RenderHandler<N>) = apply { preTransformHandlers.add(handler.cast()) }

infix fun <N : Node> N.postTransform(handler: RenderHandler<N>) = apply { postTransformHandlers.add(handler.cast()) }

infix fun <N : Node> N.preRender(handler: RenderHandler<N>) = apply { preRenderHandlers.add(handler.cast()) }

infix fun <N : Node> N.postRender(handler: RenderHandler<N>) = apply { postRenderHandlers.add(handler.cast()) }

private var _wholePosition: Node.() -> Vector3 = { wholePosition }
private var _wholeScale: Node.() -> Vector3 = { wholeScale }

operator fun Node.plusAssign(node: Node) = addChildren(node)

operator fun Node.minusAssign(node: Node) = removeChildren(node)

operator fun <N : Node> Node.plus(node: N) = node.apply { this@plus.addChildren(this) }

operator fun <N : Node> Node.minus(node: N) = node.apply { this@minus.removeChildren(this) }