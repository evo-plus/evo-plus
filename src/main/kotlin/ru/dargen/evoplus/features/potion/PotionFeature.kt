package ru.dargen.evoplus.features.potion

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.potion.PotionData
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.vigilant.FeatureCategory
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.potion.timer.PotionTimerWidget
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.PotionType
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage

object PotionFeature : Feature("potion", "Зелья", customItem(Items.POTION, 3)) {

    val PotionTimers = mutableMapOf<Int, PotionState>()
    val ComparedPotionsTimers
        get() = PotionTimers
            .mapKeys { PotionType.byOrdinal(it.key)!! }
            .asSequence()
            .sortedBy { it.value.endTime }

    val TimerWidget by widgets.widget("Зелья", "potions-timer", enabled = false, widget = PotionTimerWidget)

    var PotionsCount = 15
    var EnabledNotify = true
    var EnabledMessage = false
    var EnabledPotionsInTab  = true

    override fun FeatureCategory.setup() {
        slider(::PotionsCount, "Кол-во отображаемых зелий", "Максимальное количество отображаемых зелий в списке", max = 15)
        switch(::EnabledNotify, "Уведомление", "Уведомлять когда время действия зелья заканчивается")
        switch(::EnabledMessage, "Сообщение", "Отправлять сообщение в чат когда время действия зелья заканчивается")
        switch(::EnabledPotionsInTab, "Отображение в табе", "Показывать информацию о зельях в табе")
    }

    init {
        listen<PotionData> { potionData ->
            PotionTimers.putAll(potionData.data
                .filterValues { it.remained > 0 && it.quality > 0 }
                .mapValues { PotionState(it.value.quality, currentMillis + it.value.remained) }
            )
        }

        scheduleEvery(period = 10) {
            updatePotions()

            PotionTimerWidget.update()
        }
    }

    private fun updatePotions() {
        ComparedPotionsTimers.forEach { (potionType, potionState) ->
                val potionName = potionType.displayName
                val (quality, endTime) = potionState
                val remainTime = endTime - currentMillis

                if (remainTime < 0) {
                    if (EnabledNotify) NotifyWidget.showText("$potionName ($quality%)§c закончилось")
                    if (EnabledMessage) printMessage("$potionName ($quality%)§c закончилось")

                    PotionTimers.remove(potionType.id)
                }
            }
    }
}