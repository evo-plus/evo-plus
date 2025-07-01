package ru.dargen.evoplus.features.rune

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import pro.diamondworld.protocol.packet.ability.AbilityTimers
import pro.diamondworld.protocol.packet.rune.ActiveRunes
import ru.dargen.evoplus.feature.widget.widget
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.rune.widget.AbilityTimerWidget
import ru.dargen.evoplus.features.rune.widget.ActiveRunesWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.printMessage

object RuneFeature : Feature("rune", "Руны") {

    val Abilities = concurrentHashMapOf<String, Long>()

    var ReadyNotify = true
    var ReadyMessage = true
    var RunesBagProperties = true
    var RunesBagSet = true
    var RunesSetSwitch = true

    override fun CategoryBuilder.setup() {
        subcategory("rune-widget", "Виджеты") {
            widget("active-runes-widget", "Активные руны", ActiveRunesWidget)
            widget("active-abilities-widget", "Задержка способностей", AbilityTimerWidget, enabled = false)
        }

        subcategory("rune-notify", "Задержка способностей") {
            switch(::ReadyNotify, "Уведомление", "Отображение уведомления при окончании задержки способностей")
            switch(::ReadyMessage, "Сообщение", "Отображение сообщения при окончании задержки способностей")
        }

        subcategory("rune-set", "Сет рун") {
            switch(::RunesBagProperties, "Статистика", "Отображение статистики сета рун в мешке")
            switch(::RunesBagSet, "Активный сет рун", "Отображать активный сет рун в мешке")
            switch(::RunesSetSwitch, "Смена сетов рун", "Смена сетов рун через A-D и 1-7 в мешке")
        }
    }

    override fun initialize() {
        scheduleEvery(period = 2) {
            updateAbilities()

            AbilityTimerWidget.update()
        }

        RunesBag

        listen<ActiveRunes> { activeRunes ->
            ActiveRunesWidget.update(activeRunes.data.joinToString("\n") { " $it" })
        }

        listen<AbilityTimers> {
            it.timers
                .filterValues { it > 1000 }
                .forEach { (id, timestamp) -> Abilities[id] = currentMillis + timestamp + 600 }
        }
    }

    private fun updateAbilities() {
        Abilities.forEach { (id, timestamp) ->
            val type = AbilityType.valueOf(id) ?: return@forEach
            val remainTime = timestamp - currentMillis

            if (remainTime in 0..1000) {
                if (ReadyNotify) NotifyWidget.showText("§aСпособность \"${type.name}\" готова")
                if (ReadyMessage) printMessage("§aСпособность \"${type.name}\" готова")
                Abilities.remove(id)
            }

            if (remainTime < 0) Abilities.remove(id)
        }
    }
}
