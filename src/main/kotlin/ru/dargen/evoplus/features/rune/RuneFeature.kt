package ru.dargen.evoplus.features.rune

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.ability.AbilityTimers
import pro.diamondworld.protocol.packet.rune.ActiveRunes
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.on
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
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage
import ru.dargen.evoplus.util.minecraft.uncolored

object RuneFeature : Feature("rune", "Руны", customItem(Items.PAPER, 445)) {

    val Abilities = concurrentHashMapOf<String, Long>()

    val ActiveRunesText = text(
        " §e??? ???", " §6??? ???",
        " §6??? ???", " §a??? ???", " §a??? ???"
    ) { isShadowed = true }
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

    val RuneCooldownPattern = "Вы не можете менять сет рун ещё (\\d+) секунд.".toRegex()
    var RuneCooldown = -1L
    val RuneCooldownText = text("Кд смены рун: §a???") { isShadowed = true }
    val RuneCooldownWidget by widgets.widget("Задержка смены сета рун", "rune-cooldown", enabled = false) {
        align = v3(0.25)
        origin = Relative.CenterTop

        +RuneCooldownText
    }

    var ReadyNotify by settings.boolean("Уведомление при окончании задержки способностей", true)
    var ReadyMessage by settings.boolean("Сообщение при окончании задержки способностей", true)

    val RunesBagProperties by settings.boolean("Отображение статистики сета рун (в мешке)", true)
    val RunesBagSet by settings.boolean("Отображать активный сет рун (в мешке)", true)
    val RunesSetSwitch by settings.boolean("Смена сетов рун через A-D и 1-7 (в мешке)", true)

    init {
        scheduleEvery(period = 2) {
            updateAbilities()

            AbilityTimerWidget.update()

            if (currentMillis < RuneCooldown)
                RuneCooldownText.text = "Кд смены рун: §c${(RuneCooldown - currentMillis).asShortTextTime}"
            else RuneCooldownText.text = "Кд смены рун: §a✔"

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

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (text.startsWith("Способность ")) RuneCooldown = currentMillis + 10_000

            RuneCooldownPattern.find(text)?.let {
                val cd = it.groupValues[1].toLong()

                if (text.startsWith("Вы не можете менять сет рун ещё")) RuneCooldown = currentMillis + (cd * 1_000)
            }
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
