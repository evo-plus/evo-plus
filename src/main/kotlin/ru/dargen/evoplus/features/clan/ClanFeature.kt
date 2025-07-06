package ru.dargen.evoplus.features.clan

import net.minecraft.item.Items
import ru.dargen.evoplus.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.scheduler.async
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.minecraft.asText
import ru.dargen.evoplus.util.minecraft.displayName
import ru.dargen.evoplus.util.minecraft.lore

object ClanFeature : Feature("clan", "Клан", Items.SHIELD) {

    val BossCaptureNotify by settings.boolean(
        "Уведомление о захвате вашего босса",
        true
    )
    val InlineMenuClanScores by settings.boolean(
        "Отображать базовое К.О. боссов для захвата в меню",
        true
    )

    init {
//        listen<ClanInfo> { ClanHolder.accept(it.data) }

        Keybinds.ShareBossesToClan.on {
            async { ShareFeature.shares["bosses"]!!.share(null) }
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