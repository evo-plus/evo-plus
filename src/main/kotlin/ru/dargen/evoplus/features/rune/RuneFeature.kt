package ru.dargen.evoplus.features.rune

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.ability.AbilityTimers
import pro.diamondworld.protocol.packet.rune.ActiveRunes
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.rune.widget.AbilityTimerWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.AbilityType
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage

object RuneFeature : Feature("rune", "Руны", customItem(Items.PAPER, 445)) {

    val Abilities = concurrentHashMapOf<String, Long>()

    val ActiveRunesText = text(
        " §e??? ???", " §6??? ???",
        " §6??? ???", " §a??? ???", " §a??? ???"
    ) {
        isShadowed = true
    }
    val ActiveAbilitiesWidget by widgets.widget(
        "Задержка способностей",
        "active-abilities",
        enabled = false,
        widget = AbilityTimerWidget
    )
    val ActiveRunesWidget by widgets.widget("Надетые руны", "active-runes", enabled = false) {
        align = v3(0.25)
        origin = Relative.CenterTop

        +ActiveRunesText
    }

    var ReadyNotify = true
    var ReadyMessage = true
    var RunesBagProperties = true
    var RunesBagSet = true
    var RunesSetSwitch = true

    override fun CategoryBuilder.setup() {
        switch(::ReadyNotify, "Уведомление при окончании задержки способностей", "Отображение уведомления при окончании задержки способностей")
        switch(::ReadyMessage, "Сообщение при окончании задержки способностей", "Отображение сообщения при окончании задержки способностей")

        subcategory("set", "Сет рун") {
            switch(::RunesBagProperties, "Статистика сета рун", "Отображение статистики сета рун в мешке")
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
            ActiveRunesText.text = activeRunes.data.joinToString("\n") { " $it" }
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
