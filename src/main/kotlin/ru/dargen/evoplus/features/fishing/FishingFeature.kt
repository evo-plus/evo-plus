package ru.dargen.evoplus.features.fishing

import net.minecraft.item.Items
import net.minecraft.util.Hand
import pro.diamondworld.protocol.packet.fishing.SpotNibbles
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.fishing.widget.FishValueWidget
import ru.dargen.evoplus.features.fishing.widget.FishWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.SpotNibblesWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishQuestWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishWidgetQuestDescriptionMode
import ru.dargen.evoplus.features.fishing.widget.quest.FishWidgetQuestMode
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val Nibbles = mutableMapOf<String, Double>()
    val HourlyQuests = mutableMapOf<Int, HourlyQuestInfoHolder>()

    val ExpWidget by widgets.widget(
        "Счёт опыта рыбы", "fish-exp",
        widget = FishValueWidget("Опыт рыбы", "^Опыта дает питомцу: (\\d+)\$".toRegex())
    )
    val CaloriesWidget by widgets.widget(
        "Счёт калорийности рыбы", "fish-calories",
        widget = FishValueWidget("Каллорийность рыбы", "^Калорийность: (\\d+)\$".toRegex())
    )

    val NibblesWidget by widgets.widget(
        "Клёв на территориях", "spot-nibbles",
        widget = SpotNibblesWidget, enabled = false
    )
    val QuestsProgressWidget by widgets.widget(
        "Прогресс заданий рыбалки", "quests-progress",
        widget = FishQuestWidget, enabled = false
    )

    val QuestsProgressMode by widgets.switcher("Отображемый тип квестов", FishWidgetQuestMode.entries.toSelector())
    val QuestsProgressDescriptionMode by widgets.switcher(
        "Отображение описания квестов",
        FishWidgetQuestDescriptionMode.entries.toSelector()
    )
    val QuestsProgressVisibleMode by widgets.switcher("Отображение квестов", FishWidgetVisibleMode.entries.toSelector())

    val NibblesVisibleMode by widgets.switcher(
        "Отображение клева на территориях",
        FishWidgetVisibleMode.entries.toSelector()
    )

    val SpotsHighlight by settings.boolean("Подсветка точек клева", true)

    val HigherBitingNotify by settings.boolean("Уведомления о повышенном клёве", true)

    val AutoHookDelay by settings.selector(
        "Автоматическая удочка (задержка - тик = 50 мс)",
        (-1..40).toSelector(2), nameMapper = { if (it == -1) "отключена" else "$it" }
    )

    init {
        FishingSpotsHighlight

        //TODO: move to protocol connector
        listen<SpotNibbles> {
            Nibbles.putAll(it.nibbles)
        }
        listen<HourlyQuestInfo> { info ->
            HourlyQuests.clear()
            HourlyQuests.putAll(info.data.mapValues {
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