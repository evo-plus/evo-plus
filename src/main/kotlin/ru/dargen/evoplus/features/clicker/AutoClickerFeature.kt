package ru.dargen.evoplus.features.clicker

import net.minecraft.item.Items
import ru.dargen.evoplus.event.input.KeyEvent
import ru.dargen.evoplus.event.input.MouseClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Category
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.boundKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.selector.enumSelector
import java.util.concurrent.TimeUnit
import kotlin.math.max

object AutoClickerFeature : ru.dargen.evoplus.feature.Feature("clicker", "Кликер", icon = Items.WOODEN_SWORD) {
    
    var BindEnabled = true
    val Mode by settings.switcher(
        "Режим работы",
        enumSelector<ClickerMode>()
    )
    val Mouse by settings.switcher(
        "Кнопка мыши",
        enumSelector<ClickerMouse>()
    )
    var CPS = 10

    override fun Category.setup() {
        switch(::BindEnabled, "Статус бинда", "Включает/выключает бинд кликера")
        slider(::CPS, "КПС", "Клики в секунду", min = 1, max = 20)
    }

    private var enabled = false
    private var remainToClick = 0
        set(value) {
            field = max(0, value)
        }

    init {
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
            Mouse()
        }
    }
}