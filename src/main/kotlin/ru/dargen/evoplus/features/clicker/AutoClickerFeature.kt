package ru.dargen.evoplus.features.clicker

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.subscription
import dev.evoplus.feature.setting.property.value.Bind.Companion.key
import gg.essential.universal.UKeyboard
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import java.util.concurrent.TimeUnit
import kotlin.math.max

object AutoClickerFeature : Feature("clicker", "Кликер") {

    private var enabled = false
    private var remainToClick = 0
        set(value) {
            field = max(0, value)
        }

    var Bind = key(UKeyboard.KEY_Z)

    var Mode = ClickerMode.CLICK
    var Button = ClickerButton.LEFT
    var CPS = 10

    override fun CategoryBuilder.setup() {
        subcategory("clicker-bind", "Настройки бинда") {
            bind(::Bind, "Клавиша бинда", "Клавиша, которая включает/выключает кликер").subscription()
        }

        subcategory("clicker-settings", "Настройки кликера") {
            selector(::Mode, "Режим работы", "Выбор режима работы кликера").subscription()
            selector(::Button, "Кнопка кликера", "Выбор кнопки мыши кликера").subscription()
            slider(::CPS, "КПС", "Определённое значение кликов в секунду", range = 1..20).subscription()
        }
    }

    override fun initialize() {

        on<KeyEvent> {
            if (key != Bind.code || Mode !== ClickerMode.CLICK || !state) return@on
            enabled = !enabled
        }

        on<MouseClickEvent> {
            if (button != Bind.code || Mode !== ClickerMode.CLICK || !state) return@on
            enabled = !enabled
        }

        scheduleEvery(0, 50, unit = TimeUnit.MILLISECONDS) {
            if (!enabled && !UKeyboard.isKeyDown(Bind.code)) return@scheduleEvery

            remainToClick -= 50

            if (remainToClick > 0) return@scheduleEvery

            remainToClick = 1000 / CPS
            Button.click()
        }

    }

}
