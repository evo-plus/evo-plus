package ru.dargen.evoplus.render.context

import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.game.PreTickEvent
import ru.dargen.evoplus.event.input.KeyCharEvent
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.input.MouseWheelEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.render.node.RectangleNode
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Player
import java.util.concurrent.TimeUnit

@KotlinOpens
abstract class RenderContext : RectangleNode() {

    var translationScale = v3(1.0, 1.0, 1.0)

    init {
        registerInputHandlers()
        registerTickHandlers()
        registerRenderHandlers()
    }

    abstract fun registerRenderHandlers()

    fun registerInputHandlers() {
        on<KeyEvent> { if (allowInput()) changeKey(key, state) }
        on<KeyCharEvent> { if (allowInput()) typeChar(char, key) }

        on<MouseClickEvent> { if (allowInput()) mouseClick(mouse, button, state) }
        on<MouseWheelEvent> { if (allowInput()) mouseWheel(mouse, vWheel, hWheel) }
    }

    fun registerTickHandlers() {
        on<PreTickEvent> { preTick() }
        on<PostTickEvent> { postTick() }
        scheduleEvery(100, 100, unit = TimeUnit.MILLISECONDS) { if (Player != null) asyncTick() }
    }

    fun allowInput(): Boolean = true

}