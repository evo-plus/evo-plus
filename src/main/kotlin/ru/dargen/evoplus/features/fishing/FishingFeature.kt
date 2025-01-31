package ru.dargen.evoplus.features.fishing

import dev.evoplus.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import net.minecraft.util.Hand
import pro.diamondworld.protocol.packet.fishing.SpotNibbles
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.fishing.widget.FishingValueWidget
import ru.dargen.evoplus.features.fishing.widget.FishingValueWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.FishingWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.SpotNibblesWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishingQuestWidget
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestDescriptionMode
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestMode
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.util.minecraft.InteractionManager
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.isSink
import ru.dargen.evoplus.util.minecraft.uncolored

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val Nibbles = mutableMapOf<String, Double>()

    val ExpWidget by widgets.widget(
        "Счёт опыта рыбы", "fish-exp",
        widget = FishingValueWidget("Опыт рыбы", "^Опыта дает питомцу: (\\d+)\$".toRegex())
    )
    val CaloriesWidget by widgets.widget(
        "Счёт калорийности рыбы", "fish-calories",
        widget = FishingValueWidget("Калорийность рыбы", "^Калорийность: (\\d+)\$".toRegex())
    )

    val NibblesWidget by widgets.widget(
        "Клёв на территориях", "spot-nibbles",
        widget = SpotNibblesWidget, enabled = false
    )
    val QuestsProgressWidget by widgets.widget(
        "Прогресс заданий рыбалки", "quests-progress",
        widget = FishingQuestWidget, enabled = false
    )

    var SpotsHighlight = true
    var HigherBitingNotify = true
    var AutoHookDelay = 1

    var QuestsProgressVisibleMode = FishingWidgetVisibleMode.ENABLED
    var QuestsProgressMode = FishingWidgetQuestMode.ALL
    var QuestsProgressDescriptionMode = FishingWidgetQuestDescriptionMode.ENABLED
    var NibblesVisibleMode = FishingWidgetVisibleMode.ENABLED
    var ValueVisibleMode = FishingValueWidgetVisibleMode.ENABLED

    override fun CategoryBuilder.setup() {
        switch(::SpotsHighlight, "Подсветка точек клева", "Подсвечивает точки клева на локации")
        switch(::HigherBitingNotify, "Уведомления о повышенном клёве", "Уведомляет о повышенном клёве на локациях")
        slider(::AutoHookDelay, "Автоматическая удочка", "Автоматически подбирает удочку (тик = 50 мс)", range = -1..40)

        subcategory("widget", "Настройки виджетов") {
            selector(::QuestsProgressVisibleMode, "Отображение квестов", "Отображает виджет квестов рыбалки при определённых условиях")
            selector(::QuestsProgressMode, "Отображаемый тип квестов", "Отображает задания рыбалки при определённых условиях")
            selector(::QuestsProgressDescriptionMode, "Отображение описания квестов", "Отображает описание заданий рыбалки при определённых условиях")
            selector(::NibblesVisibleMode, "Клёв на территориях", "Отображает виджет процента клёва на локациях при определённых условиях")
            selector(::ValueVisibleMode, "Отображение опыта и калорийности рыбы", "Отображает виджеты количества опыта и калорийности рыбы")
        }
    }

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
                NotifyWidget.showText("На локации §6${groupValues[1]}", "повышенный клёв.")
            }
        }
    }

}