package ru.dargen.evoplus.features.fishing.widget.quest

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.render.node.*
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.customItem

data object FishQuestWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
        asyncTick {
            render = FishingFeature.QuestsProgressVisibleMode.isVisible() || isWidgetEditor
            update()
        }
    }

    fun update() {
        node._children = FishingFeature.HourlyQuests.values
            .filter { FishingFeature.QuestsProgressMode.isVisible(it.type) }
            .filter { it.timestamp > currentMillis }
            .ifEmpty { if (isWidgetEditor) takePreviewQuests() else emptyList() }
            .groupBy { it.type }
            .flatMap { (_, quests) ->
                quests.mapIndexed { index, info ->
                    val isCompleted = info.progress >= info.needed
                    val isClaimed = info.progress < 0
                    val isMoreDescription = !isClaimed && !isCompleted

                    val remainTime = (info.timestamp - currentMillis).coerceAtLeast(0L)

                    val text = buildList {
                        add(" ${(if (info.type == "NETHER") "§c" else "§a")}№${index + 1} §7${remainTime.asShortTextTime} ")
                        if (isMoreDescription) {
                            if (FishingFeature.QuestsProgressDescriptionMode.isVisible()) add(" ${info.lore}")
                            add(" §9Прогресс: ${info.progress}/${info.needed}")
                        }
                    }

                    hbox {
                        space = 2.0
                        indent = v3()

                        +item(icon(isCompleted, isClaimed)) { scale = scale(.7, .7) }
                        +text(text) { isShadowed = true }

                        recompose()
                    }
                }
            }.toMutableList()
        node.recompose()
    }

    private fun icon(completed: Boolean, claimed: Boolean) =
        customItem(Items.PAPER, if (completed) 373 else if (claimed) 374 else 372)

    private fun takePreviewQuests() = HourlyQuestType.values
        .filter { FishingFeature.QuestsProgressMode.isVisible(it.type) }
        .take(4)
        .map { HourlyQuestInfoHolder(it, HourlyQuestInfo.HourlyQuest(it.id, 0, 111111)) }

}