package ru.dargen.evoplus.features.clicker

import net.minecraft.item.Items
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.boundKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.leftClick
import ru.dargen.evoplus.util.minecraft.rightClick
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector
import kotlin.math.max

object AutoClickerFeature : Feature("clicker", "Кликер", Items.WOODEN_SWORD) {

    val BindEnabled by settings.boolean("Статус бинда")
    val Mode by settings.switcher(
        "Режим работы",
        enumSelector<ClickerMode>()
    )
    val Mouse by settings.switcher(
        "Кнопка мыши",
        enumSelector<ClickerMouse>()
    )
    val CPS by settings.selector(
        "Кликов в секунду",
        (1..20).toSelector()
    ) { "$it" }

    private var enabled = false
    private var remainToClick = 0
        set(value) {
            field = max(0, value)
        }

    var leftClickTimer = 0
    var rightClickTimer = 0

    init {

        Keybinds.AutoClicker.on {
            if (!BindEnabled || Mode !== ClickerMode.CLICK) return@on
            enabled = !enabled

            if (enabled) {
                leftClickTimer = 20 / CPS
                rightClickTimer = 20 / CPS
                Client.options.attackKey.isPressed = false
                Client.options.useKey.isPressed = false
            } else {
                Client.options.attackKey.isPressed = false
                Client.options.useKey.isPressed = false
            }
        }
        
        on<KeyEvent> {
            if (key != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }

        on<MouseClickEvent> {
            if (button != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }

//        scheduleEvery(0, 50, unit = TimeUnit.MILLISECONDS) {
//            if (!enabled) return@scheduleEvery
//
//            remainToClick -= 50
//
//            if (remainToClick > 0) return@scheduleEvery
//
//            remainToClick = 1000 / CPS
//            Mouse()
//        }

        on<PostTickEvent> {

            if (!enabled) return@on

            when (Mode) {
                ClickerMode.HOLD -> {
                    if (Mouse == ClickerMouse.LEFT) Client.options.attackKey.isPressed = true
                    else Client.options.useKey.isPressed = true
                }
                ClickerMode.CLICK -> {
                    if (Mouse == ClickerMouse.LEFT) {
                        leftClickTimer--

                        if (leftClickTimer <= 0) {
                            leftClick()
                            leftClickTimer = 20 / CPS
                        }
                    } else {
                        rightClickTimer--

                        if (rightClickTimer <= 0) {
                            rightClick()
                            rightClickTimer = 20 / CPS
                        }
                    }
                }
            }

        }

    }

}
