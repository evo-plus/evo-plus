package ru.dargen.evoplus.features.boss

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import pro.diamondworld.protocol.packet.boss.BossTimers
import pro.diamondworld.protocol.packet.game.GameEvent.EventType.MYTHICAL_EVENT
import ru.dargen.evoplus.event.evo.data.GameEventChangeEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.widget.widget
import ru.dargen.evoplus.features.boss.BossTimerFeature.MaxLevel
import ru.dargen.evoplus.features.boss.BossTimerFeature.MinLevel
import ru.dargen.evoplus.features.boss.widget.BossTimerWidget
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.format.fromTextTime
import ru.dargen.evoplus.util.minecraft.*
import kotlin.math.absoluteValue

private const val MYTHICAL_EVENT_MULTIPLIER = 1.5384615384615

object BossTimerFeature : Feature("boss-timer", "Таймер боссов") {

    val AlertedBosses = mutableSetOf<String>()
    val PreAlertedBosses = mutableSetOf<String>()
    val Bosses: MutableMap<String, Long> by config("bosses", hashMapOf())
    val ComparedBosses
        get() = Bosses
            .mapKeys { BossType.valueOf(it.key) }
            .filter { it.key.inLevelBounds }
            .mapKeys { it.key!! }
            .asSequence()
            .sortedBy { it.value }

    var PremiumTimer = false

    var MinLevel = 0
    var MaxLevel = 520
    var BossesCount = 60
    var WidgetTeleport = false

    var ShortName = false
    var ShortTimeFormat = false
    var InlineMenuTime = true
    var PostSpawnShowTime = 0

    var PreSpawnNotify = true
    var SpawnNotify = true
    var UpdateNotify = true

    var PreSpawnAlertTime = 0
    var PreSpawnMessage = false
    var SpawnMessage = false
    var PreSpawnClanMessage = false
    var SpawnClanMessage = false

    var OnlyRaidBosses = false
    var OnlyCapturedBosses = false

    override fun CategoryBuilder.setup() {
        switch(::PremiumTimer, "Покупной таймер", "Включите эту опцию, если вы приобрели его")
        widget("boss-timer", "Таймер боссов", BossTimerWidget)
        button("Сбросить таймеры", text = "Сбросить") { Bosses.clear() }

        subcategory("widget", "Настройки виджета") {
            slider(
                ::MinLevel,
                "Мин. уровень босса",
                "Минимальный уровень босса, которые будут отображаться в виджете",
                range = 0..520 step 5
            )
            slider(
                ::MaxLevel,
                "Макс. уровень босса",
                "Максимальный уровень босса, которые будут отображаться в виджете",
                range = 0..520 step 5
            )
            slider(
                ::BossesCount,
                "Отображаемые боссы",
                "Количество отображаемых боссов в виджете",
                range = 0..60
            )
            switch(::WidgetTeleport, "Телепорт по клику в виджете", "Телепортирует к определённому боссу по клику в виджете")
        }

        subcategory("visual", "Отображение") {
            switch(
                ::ShortName,
                "Сокращение имени босса",
                "Сокращённый формат имени босса в виджете \n(Лавовый монстр [360] -> [360])"
            )
            switch(
                ::ShortTimeFormat,
                "Сокращенный формат времени",
                "Сокращённый формат времени в виджете \n(1ч 30мин 15сек -> 1:30:15)"
            )
            switch(::InlineMenuTime, "Время до спавна в меню", "Отображает время до спавна босса в меню (/bosses)")
            slider(
                ::PostSpawnShowTime,
                "Сохранять в таймере после спавна",
                "Сохраняет в виджете информацию о боссе после его респавна",
                range = 0..360 step 5
            )
        }

        subcategory("notify", "Уведомления") {
            switch(::PreSpawnNotify, "Уведомление до спавна")
            switch(::SpawnNotify, "Уведомление о спавне")
            switch(::UpdateNotify, "Уведомление об обновлении времени")
        }

        subcategory("message", "Сообщения") {
            slider(::PreSpawnAlertTime, "Предупреждать о боссе", "Отправляет сообщение до респавна босса (в секундах)", range = 0..360 step 5)
            switch(::PreSpawnMessage, "Сообщение до спавна")
            switch(::SpawnMessage, "Сообщение о спавне")
            switch(::PreSpawnClanMessage, "Сообщение до спавна в клановый чат")
            switch(::SpawnClanMessage, "Сообщение о спавне в клановый чат")
        }

        subcategory("filter", "Фильтры") {
            switch(::OnlyRaidBosses, "Отображать только рейдовых боссов", "Отображает только рейдовых боссов в виджете")
            switch(
                ::OnlyCapturedBosses,
                "Отображать только захваченных боссов",
                "Отображает только захваченных кланом боссов в виджете"
            )
        }
    }

    override fun initialize() {

        on<GameEventChangeEvent> {
            if (old === MYTHICAL_EVENT || new === MYTHICAL_EVENT) Bosses.replaceAll { bossId, spawn ->
                if (BossType.valueOf(bossId)?.isRaid == false) return@replaceAll spawn

                (if (old === MYTHICAL_EVENT) spawn * MYTHICAL_EVENT_MULTIPLIER else spawn / MYTHICAL_EVENT_MULTIPLIER).toLong()
            }
        }

        listen<BossTimers> {
            if (PremiumTimer) it.timers
                .mapKeys { BossType.valueOf(it.key) ?: return@listen }
                .mapValues { (it.value + currentMillis * if (PlayerDataCollector.event === MYTHICAL_EVENT && it.key.isRaid) MYTHICAL_EVENT_MULTIPLIER else 1.0).toLong() }
                .mapKeys { it.key.id }
                .let(Bosses::putAll)
        }

        scheduleEvery(period = 10) {
            if (!PremiumTimer) fillBossData()

            fillInventory()
            updateBosses()

            BossTimerWidget.update()
        }
    }

    fun updateBosses() {
        ComparedBosses.forEach { (type, timestamp) ->
            val displayName = type.displayName
            val remainTime = timestamp - currentMillis

            if (type.inLevelBounds
                && PreSpawnAlertTime > 0
                && type.id !in PreAlertedBosses
                && remainTime / 1000 == PreSpawnAlertTime.toLong()
            ) {
                val timeText = remainTime.asTextTime

                PreAlertedBosses.add(type.id)

                if (type.inLevelBounds) {
                    if (PreSpawnMessage) message("§aБосс §6$displayName §aвозродится через §6$timeText", type)
                    if (PreSpawnClanMessage) sendClanMessage("§aБосс §6$displayName §aвозродится через §6$timeText")
                    if (PreSpawnNotify) notify(type, "Босс §6$displayName", "§fчерез §6$timeText")
                }
            }

            if (remainTime <= 0) {
                if (remainTime <= -PostSpawnShowTime * 1000) {
                    Bosses.remove(type.id)
                    PreAlertedBosses.remove(type.id)
                    AlertedBosses.remove(type.id)
                }

                if (!type.inLevelBounds || type.id in AlertedBosses || remainTime !in -2000..0) return@forEach

                AlertedBosses.add(type.id)
                if (SpawnMessage) message("§aБосс §6$displayName §aвозродился.", type)
                if (SpawnClanMessage) sendClanMessage("§aБосс $displayName §aвозродился.")
                if (SpawnNotify) notify(type, "Босс §6$displayName §fвозродился")
            }
        }
    }

    private fun fillInventory() {
        if (!InlineMenuTime) return

        val screen = CurrentScreen

        if (screen !is GenericContainerScreen || !BossFeature.BossMenuPattern.containsMatchIn(screen.title.string.uncolored())) return

        screen.screenHandler.stacks.forEach {
            val type = BossType.valueOfName(it.displayName?.string ?: return@forEach) ?: return@forEach
            val timeText = ((Bosses[type.id] ?: return@forEach) - currentMillis).asTextTime

            val resetText = "§fВозрождение: §e$timeText".asText()

            it.lore = it.lore.toMutableList().run {
                if (getOrNull(1)?.string?.contains("Возрождение") == true) set(1, resetText)
                else return@run (listOf(first(), resetText) + drop(1))
                this
            }
        }
    }

    private fun fillBossData() {
        val (type, additionTime) = fetchWorldBossData() ?: return

        val spawnTime = currentMillis + additionTime
        val currentSpawnTime = Bosses[type.id] ?: 0

        if ((spawnTime - currentSpawnTime).absoluteValue < 13000) return

        if (UpdateNotify) NotifyWidget.showText(
            "Босс §6${type.displayName} §fобновлен",
            "Возрождение через §6${additionTime.asTextTime}"
        )
        Bosses[type.id] = spawnTime.fixSeconds
    }

    private fun fetchWorldBossData() = Client?.world?.entities
        ?.filterNotNull()
        ?.mapNotNull { it.displayName?.string?.uncolored() }
        ?.sortedByDescending { it.startsWith("Босс") }
        ?.mapNotNull {
            when {
                it.startsWith("Босс") -> it.substring(5)
                "сек." in it || "мин." in it || "ч." in it -> it
                else -> null
            }
        }
        ?.run {
            val type = BossType.valueOfName(getOrNull(0) ?: return@run null) ?: return@run null
            val delay = getOrNull(1)?.replace("۞", "")?.fromTextTime
                ?.let { if (PlayerDataCollector.event === MYTHICAL_EVENT && type.isRaid) (it / MYTHICAL_EVENT_MULTIPLIER).toLong() else it }
                ?.takeIf { it > 6000 }
                ?: return@run null

            type to delay
        }

    fun message(text: String, type: BossType) =
        printHoveredCommandMessage(text, "§aНажмите, чтобы начать телепортацию", "/boss ${type.level}")

    fun notify(type: BossType, vararg text: String) = NotifyWidget.showText(*text) { sendCommand("boss ${type.level}") }

}

val BossType?.inLevelBounds get() = this?.level in MinLevel..MaxLevel

private val Long.fixSeconds get() = (this / 1000) * 1000