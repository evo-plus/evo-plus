package ru.dargen.evoplus.features.boss

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.boss.BossDamage
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.Bosses
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.mixin.render.hud.BossBarHudAccessor
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.sendClanMessage
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit

object BossFeature : Feature("boss", "Боссы", Items.DIAMOND_SWORD) {

    private val BossCursedPattern = "Босс проклят! Особенность: ([а-яА-ЯёЁ ]+)".toRegex()
    private val BossCapturePattern = "^Босс (.*) захвачен кланом (.*)!\$".toRegex()
    private val ClanWavePattern = "Испытание вызова (?:(|\\d+):|)(\\d+)".toRegex()

    private val BossHealthsPattern = "^([А-Яа-яЁё ]+) (\\d+(\\.\\d+)?)❤\$".toRegex()
    val BossMenuPattern = "[넼넽넾]".toRegex()

    val BossDamageText = text("???? [??]: ??\uE35E") { isShadowed = true }

    val BossDamageWidget by widgets.widget("Урон по боссу", "boss-damage") {
        origin = Relative.CenterBottom
        align = v3(.58, .9)
        +BossDamageText
    }

    var NotifyCapture = true
    var CurseMessage = false
    var BossLowHealthsMessage = false
    var BossHealthsPercent = .5f
    var BossHealthsCooldown = 15

    override fun CategoryBuilder.setup() {
        subcategory("boss-notify", "Оповещения") {
            switch(::NotifyCapture, "Захват боссов", "Уведомляет о захвате боссов")
            switch(::CurseMessage, "Проклятие босса", "Отправляет сообщение о проклятии босса в клановый чат")
            switch(::BossLowHealthsMessage, "Процент здоровья босса", "Отправляет сообщение об определённом проценте здоровья босса в клановый чат")
        }

        subcategory("boss-health-settings", "Настройки оповещений здоровья босса") {
            percent(::BossHealthsPercent, "Оповещать о здоровье босса", "Процент здоровья босса, при котором отправляется сообщение в клановый чат")
            slider(::BossHealthsCooldown, "Частота оповещений о здоровье босса", "Частота отправки сообщения о здоровье босса в клановый чат (в секундах)", range = 5..60)
        }
    }

    override fun initialize() {

        listen<BossDamage> {
            val type = BossType.valueOf(it.id) ?: return@listen
            BossDamageText.text = "${type.displayName}: §c${it.count}§r\uE35E"
        }

        on<ChatReceiveEvent> {
            if (NotifyCapture) BossCapturePattern.find(text)?.run {
                val type = BossType.valueOfName(groupValues[1]) ?: return@run
                val clan = groupValues[2]

                NotifyWidget.showText("Босс ${type.displayName}§f захвачен", "кланом $clan.")
            }
            if (CurseMessage) BossCursedPattern.find(text)?.run {
                val type = PlayerDataCollector.location.bossType ?: return@on
                val curse = groupValues[1]
                sendClanMessage("§8[§e${Connector.server.displayName}§8] §a${type.displayName} §3проклят на $curse")
            }
        }

        scheduleEvery(unit = TimeUnit.SECONDS) {
            if (it.executions % BossHealthsCooldown != 0) return@scheduleEvery

            getFilteredBossBars()?.firstNotNullOfOrNull {
                if (!BossLowHealthsMessage) return@scheduleEvery
                val text = it.name.string.uncolored().trim()

                if (ClanWavePattern.containsMatchIn(text)) return@scheduleEvery

                BossHealthsPattern.find(text)?.run {
                    val percent = it.percent

                    if (percent >= BossHealthsPercent) return@run

                    val isCursed = it.name.siblings.any { it.style.color?.name == "#25D192" }
                    val type = BossType.valueOfName(groupValues[1]) ?: return@run
                    val health = groupValues[2].toDoubleOrNull() ?: return@run

                    sendClanMessage("§8[§e${Connector.server.displayName}§8] ${type.displayName}${if (isCursed) " §8[§3Прок§8]" else ""}§8: §e${percent.fix()}% §8(§c${health.fix()}❤§8)")
                }
            } ?: return@scheduleEvery
        }

        ShareFeature.createOf<Map<String, Long>>(
            "bosses", "Таймеры боссов",
            { Bosses.mapValues { it.value - currentMillis } }
        ) { nick, data ->
            val shared = data
                .mapKeys { BossType.valueOf(it.key) ?: return@createOf }
                .mapValues { it.value + currentMillis }

            NotifyWidget.showText(
                "§6$nick §fотправил вам боссов §7(${shared.size}).",
                "Нажмите, чтобы принять.",
                delay = 10.0,
                action = { BossReceiveScreen.open(shared) }
            )
        }
    }

    private fun getFilteredBossBars() = Client?.inGameHud?.bossBarHud?.cast<BossBarHudAccessor>()?.bossBars?.values
        ?.filter { it.name.string.uncolored().trim().isNotEmpty() }
}