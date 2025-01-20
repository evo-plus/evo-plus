package ru.dargen.evoplus.features.fishing.widget.quest

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.render.Tips
import ru.dargen.evoplus.render.node.asyncTick
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.postRender
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.customItem

data object FishingQuestWidget : WidgetBase {
    
    override val node = vbox {
        indent = v3()
        
        asyncTick {
            render = FishingFeature.QuestsProgressVisibleMode.isVisible() || isWidgetEditor
        }
    }
    
    fun update(quests: Collection<HourlyQuestInfoHolder> = emptyList()) {
        node._childrens = quests
            .filter { FishingFeature.QuestsProgressMode.isVisible(it.type) }
            .filter { it.timestamp > currentMillis }
            .ifEmpty { if (isWidgetEditor) takePreviewQuests() else emptyList() }
            .groupBy { it.type }
            .flatMap { (_, quests) ->
                quests.mapIndexed { index, info ->
                    hbox {
                        val descriptionMode = FishingFeature.QuestsProgressDescriptionMode
                        
                        space = 2.0
                        indent = v3()
                        
                        +item(customItem(Items.PAPER, if (!info.isAvailable) 374 else 372)) { scale = scale(.7, .7) }
                        +text {
                            isShadowed = true
                            postRender { _, _ ->
                                val remainTime = (info.timestamp - currentMillis).coerceAtLeast(0L)
                                
                                val text = buildList {
                                    add(" ${(if (info.type == "NETHER") "§c" else "§a")}№${index + 1} §7${remainTime.asShortTextTime} ")
                                    if (info.isAvailable && descriptionMode.isVisible()) add(" ${info.lore}")
                                    
                                    if (info.isCompleted) add(" §aЗаберите награду")
                                    else if (!info.isClaimed) add(" §9Прогресс: ${info.progress}/${info.needed}")
                                }
                                
                                this.text = text.joinToString("\n")
                            }
                        }
                        
                        
                        if (descriptionMode === FishingWidgetQuestDescriptionMode.HOVER) postRender { mat, _ ->
                            if (isHovered && CurrentScreen != null && !isWidgetEditor) Tips.draw(mat, info.lore)
                        }
                        
                        recompose()
                    }
                }
            }.toMutableList()
        
        node.recompose()
    }
    
    fun takePreviewQuests() = HourlyQuestType.values
        .filter { FishingFeature.QuestsProgressMode.isVisible(it.type) }
        .take(4)
        .map { HourlyQuestInfoHolder(it, HourlyQuestInfo.HourlyQuest(it.id, 0, 111111)) }

}