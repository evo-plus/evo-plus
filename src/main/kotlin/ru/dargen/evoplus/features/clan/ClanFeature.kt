package ru.dargen.evoplus.features.clan

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.minecraft.asText
import ru.dargen.evoplus.util.minecraft.displayName
import ru.dargen.evoplus.util.minecraft.lore
import ru.dargen.evoplus.util.minecraft.uncolored

object ClanFeature : Feature("clan", "Клан", Items.SHIELD) {

    private val BossCapturePattern =
        "\\[Клан] Клан (\\S+) начал захват вашего босса ([\\s\\S]+)\\. Защитите его\\.".toRegex()

    var BossCaptureNotify = true
    var InlineMenuClanScores = true

    override fun CategoryBuilder.setup() {
        subcategory("clan-notify", "Уведомления") {
            switch(::BossCaptureNotify, "Захват вашего босса", "Уведомляет о перехвате вашего босса")
        }

        subcategory("clan-visual", "Визуализация") {
            switch(::InlineMenuClanScores, "К.О. для захвата босса в меню", "Отображает базовое кол-во К.О. для захвата босса в меню")
        }
    }

    override fun initialize() {
//        listen<ClanInfo> { ClanHolder.accept(it.data) }

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (BossCaptureNotify) BossCapturePattern.find(text)?.run {
                val clan = groupValues[1]
                val bossName = groupValues[2]
                val bossType = BossType.valueOfName(bossName) ?: return@run

                BossTimerFeature.notify(
                    bossType,
                    "Клан §6$clan§f пытается захватить",
                    "вашего босса ${bossType.displayName}"
                )
            }
        }

        on<InventoryFillEvent> {
            if (InlineMenuClanScores && BossFeature.BossMenuPattern.containsMatchIn(openEvent?.nameString ?: "")) {
                contents.forEach {
                    val type = BossType.valueOfName(it.displayName?.string ?: return@forEach) ?: return@forEach
                    if (it.lore.none { it.string.contains("Очков для захвата") }) {
                        val capturePoints = type.capturePoints
                        val additionalPointsMultiplier = ClanInfoCollector.Bosses.size * .03
                        val additionalPoints = additionalPointsMultiplier * capturePoints

                        val baseClanScoreText = "§fОчков для захвата: §e${(capturePoints + additionalPoints).toInt()}${
                            if (additionalPointsMultiplier > 0) " §c${(additionalPointsMultiplier * 100).fix()}%"
                            else ""
                        } §8($capturePoints${if (additionalPointsMultiplier > 0) " * " + additionalPointsMultiplier.fix() else ""})"

                        it.lore = (listOf(
                            it.lore.first(),
                            baseClanScoreText.asText()
                        ) + it.lore.drop(1))
                    }
                }
            }
        }

    }

}