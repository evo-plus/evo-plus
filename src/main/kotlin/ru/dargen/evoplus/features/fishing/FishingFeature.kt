package ru.dargen.evoplus.features.fishing

import net.minecraft.item.Items
import net.minecraft.util.Hand
import pro.diamondworld.protocol.packet.fishing.SpotNibbles
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.fishing.widget.FishingValueWidget
import ru.dargen.evoplus.features.fishing.widget.FishingWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.SpotNibblesWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishingQuestWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestDescriptionMode
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestMode
import ru.dargen.evoplus.features.misc.notify.Notifies
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.minecraft.InteractionManager
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.isSink
import ru.dargen.evoplus.util.minecraft.uncolored
import ru.dargen.evoplus.util.selector.toSelector

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val Nibbles = mutableMapOf<String, Double>()

    val ExpWidget by widgets.widget(
        "Счёт опыта рыбы", "fish-exp",
        widget = FishingValueWidget("Опыт рыбы", "^Опыта дает питомцу: (\\d+)\$".toRegex())
    )
    val CaloriesWidget by widgets.widget(
        "Счёт калорийности рыбы", "fish-calories",
        widget = FishingValueWidget("Каллорийность рыбы", "^Калорийность: (\\d+)\$".toRegex())
    )

    val NibblesWidget by widgets.widget(
        "Клёв на территориях", "spot-nibbles",
        widget = SpotNibblesWidget, enabled = false
    )
    val QuestsProgressWidget by widgets.widget(
        "Прогресс заданий рыбалки", "quests-progress",
        widget = FishingQuestWidget, enabled = false
    )
    
    val QuestsProgressMode by widgets.switcher("Отображемый тип квестов", FishingWidgetQuestMode.entries.toSelector())
    val QuestsProgressDescriptionMode by widgets.switcher(
        "Отображение описания квестов",
        FishingWidgetQuestDescriptionMode.entries.toSelector()
    )
    val QuestsProgressVisibleMode by widgets.switcher("Отображение квестов", FishingWidgetVisibleMode.entries.toSelector())

    val NibblesVisibleMode by widgets.switcher(
        "Отображение клева на территориях",
        FishingWidgetVisibleMode.entries.toSelector()
    )

    val SpotsHighlight by settings.boolean("Подсветка точек клева", true)

    val HigherBitingNotify by settings.boolean("Уведомления о повышенном клёве", true)

    val AutoHookDelay by settings.selector(
        "Автоматическая удочка (задержка - тик = 50 мс)",
        (-1..40).toSelector(2), nameMapper = { if (it == -1) "отключена" else "$it" }
    )

    init {
        FishingQuestWidget.update(FishingQuestWidget.takePreviewQuests())
        FishingSpotsHighlight

        //TODO: move to protocol connector
        listen<SpotNibbles> {
            Nibbles.putAll(it.nibbles)
        }
        listen<HourlyQuestInfo> { info ->
            FishingQuestWidget.update(info.data.map {
                HourlyQuestInfoHolder(
                    HourlyQuestType.byOrdinal(it.key)!!,
                    it.value
                )
            })
        }

        var fishHookTicks = 0
        on<PostTickEvent> {
            if (AutoHookDelay >= 0 && Player?.fishHook?.isSink == true) {
                if (++fishHookTicks >= AutoHookDelay) {
                    fishHookTicks = 0
                    Player?.fishHook?.kill()
                    InteractionManager?.interactItem(Player!!, Hand.MAIN_HAND)
                }
            } else fishHookTicks = 0
        }

        on<ChatReceiveEvent> {
            if (HigherBitingNotify) HigherBitingPattern.find(text.uncolored())?.run {
                Notifies.showText("На локации §6${groupValues[1]}", "повышенный клёв.")
            }
        }
    }

}