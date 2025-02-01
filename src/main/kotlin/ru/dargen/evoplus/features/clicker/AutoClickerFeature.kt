package ru.dargen.evoplus.features.clicker

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.subscription
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.boundKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import java.util.concurrent.TimeUnit
import kotlin.math.max

object AutoClickerFeature : Feature("clicker", "Кликер") {

    private var enabled = false
    private var remainToClick = 0
        set(value) {
            field = max(0, value)
        }

    var BindEnabled = true
    var Mode = ClickerMode.CLICK
    var Button = ClickerButton.LEFT
    var CPS = 10

    override fun CategoryBuilder.setup() {
        switch(::BindEnabled, "Статус бинда", "Включает/выключает бинд кликера").subscription()
        selector(::Mode, "Режим работы", "Выбор режима работы кликера").subscription()
        selector(::Button, "Кнопка кликера", "Выбор кнопки мыши кликера").subscription()
        slider(::CPS, "КПС", "Определённое значение кликов в секунду", range = 1..20).subscription()
    }

    override fun initialize() {
        Keybinds.AutoClicker.on {
            if (!BindEnabled || Mode !== ClickerMode.CLICK) return@on
            enabled = !enabled
        }
        
        on<KeyEvent> {
            if (key != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }
        on<MouseClickEvent> {
            if (button != Keybinds.AutoClicker.boundKey.code || !BindEnabled || Mode !== ClickerMode.HOLD) return@on
            enabled = state
        }

        scheduleEvery(0, 50, unit = TimeUnit.MILLISECONDS) {
            if (!enabled) return@scheduleEvery

            remainToClick -= 50

            if (remainToClick > 0) return@scheduleEvery

            remainToClick = 1000 / CPS
            Button.click()
        }
    }
}