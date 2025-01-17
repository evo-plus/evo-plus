package ru.dargen.evoplus.features.fishing

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import pro.diamondworld.protocol.packet.fishing.SpotNibbles
import pro.diamondworld.protocol.packet.fishing.quest.HourlyQuestInfo
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ParticleEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.fishing.widget.NetherProgressWidget
import ru.dargen.evoplus.features.fishing.widget.NormalProgressWidget
import ru.dargen.evoplus.features.fishing.widget.SpotNibblesWidget
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.info.holder.HourlyQuestInfoHolder
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.HourlyQuestType
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.plus
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.render.node.tick
import ru.dargen.evoplus.render.node.world.CubeOutlineNode
import ru.dargen.evoplus.render.node.world.cubeOutline
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*
import ru.dargen.evoplus.util.selector.toSelector
import java.util.concurrent.TimeUnit
import kotlin.math.max

object FishingFeature : Feature("fishing", "Рыбалка", Items.FISHING_ROD) {

    val BackpackTitle = "\uE974"
    val TradeTitle = "\uE114"
    val FishExpPattern = "^Опыта дает питомцу: (\\d+)\$".toRegex()
    val FishCaloriesPattern = "^Калорийность: (\\d+)\$".toRegex()
    val HigherBitingPattern = "^На локации \"([\\S\\s]+)\" повышенный клёв!\$".toRegex()

    val Nibbles = mutableMapOf<String, Double>()
    val HourlyQuests = mutableMapOf<Int, HourlyQuestInfoHolder>()

    val NibblesOnlyOnFish by settings.boolean("Виджет клёва только на рыбалке", false)
    val NibblesWidget by widgets.widget(
        "Клёв на территориях",
        "spot-nibbles",
        widget = SpotNibblesWidget,
        enabled = false
    )
    val FishExpWidget by widgets.widget("Счёт опыта рыбы", "fish-exp") {
        val fishExpText = +text("Опыт питомцам: 0") {
            isShadowed = true
        }

        tick {
            val currentScreen = CurrentScreen
            val isCraftingInventory = (currentScreen is InventoryScreen)
            val isBackpackInventory =
                (currentScreen is GenericContainerScreen && BackpackTitle in currentScreen.title.string)
            val isTradeInventory = (currentScreen is GenericContainerScreen && TradeTitle in currentScreen.title.string)

            render = isWidgetEditor || isCraftingInventory || isBackpackInventory || isTradeInventory

            if (!isCraftingInventory && !isBackpackInventory && !isTradeInventory) return@tick

            val exp = max(
                currentScreen
                    .safeCast<GenericContainerScreen>()
                    ?.screenHandler
                    ?.stacks
                    ?.findExp() ?: 0,
                Player!!.inventory.items.findExp()
            )

            fishExpText.text = "Опыт питомцам: $exp"
        }
    }
    val FishCaloriesWidget by widgets.widget("Счёт общей калорийности рыбы", "fish-calories") {
        val fishExpText = +text("Общая калорийность: 0") {
            isShadowed = true
            //TODO: сделать поверх бекграунда инвентаря майнкрафта
        }

        tick {
            val currentScreen = CurrentScreen
            val isCraftingInventory = (currentScreen is InventoryScreen)
            val isBackpackInventory =
                (currentScreen is GenericContainerScreen && BackpackTitle in currentScreen.title.string)
            val isTradeInventory = (currentScreen is GenericContainerScreen && TradeTitle in currentScreen.title.string)

            render = isWidgetEditor || isCraftingInventory || isBackpackInventory || isTradeInventory

            if (!isCraftingInventory && !isBackpackInventory && !isTradeInventory) return@tick

            val calories = max(
                currentScreen
                    .safeCast<GenericContainerScreen>()
                    ?.screenHandler
                    ?.stacks
                    ?.findCalories() ?: 0,
                Player!!.inventory.items.findCalories()
            )

            fishExpText.text = "Общая калорийность: $calories"
        }
    }
    val NormalQuestsProgressWidget by widgets.widget(
        "Прогресс заданий (Обычный мир)",
        "normal-quests-progress",
        enabled = false,
        widget = NormalProgressWidget
    )
    val NetherQuestsProgressWidget by widgets.widget(
        "Прогресс заданий (Ад)",
        "nether-quests-progress",
        enabled = false,
        widget = NetherProgressWidget
    )

    val LoreProgressTips by settings.boolean("Показывать описание при наведении", true)
    val AutoFish by settings.boolean("Автоматическая удочка", true)
    val HookDelay by settings.selector("Задержка удочки (тик = 50 мс)", (0..40).toSelector(1))
    val HigherBitingNotify by settings.boolean("Уведомления о повышенном клёве", true)

    init {
        scheduleEvery(unit = TimeUnit.SECONDS) {
            SpotNibblesWidget.update()
            NormalProgressWidget.update()
            NetherProgressWidget.update()
        }

        val nodes = mutableMapOf<Long, CubeOutlineNode>()
        on<ParticleEvent> {
            with(packet) {
                nodes.getOrPut(BlockPos.asLong(x.toInt(), y.toInt(), z.toInt())) {
                    WorldContext + cubeOutline {
                        position = v3(x, y, z)
                        size = v3(offsetX.toDouble(), offsetY.toDouble(), offsetZ.toDouble()) * 2.0
                        origin = v3(.5, .5, .5)
                        color = Colors.Yellow
                    }
                }
            }
        }

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
            if (AutoFish && Player?.fishHook?.isSink == true) {
                if (++fishHookTicks >= HookDelay) {
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

    private fun Collection<ItemStack>.findExp() = mapNotNull { item ->
        item.lore.getOrNull(2)
            ?.string
            ?.let { FishExpPattern.find(it.trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.times(item.count) }
    }.sum().takeIf { it > 0 } ?: 0

    private fun Collection<ItemStack>.findCalories() = mapNotNull { item ->
        item.lore.getOrNull(2)
            ?.string
            ?.let { FishCaloriesPattern.find(it.trim())?.groupValues?.getOrNull(1)?.toIntOrNull()?.times(item.count) }
    }.sum().takeIf { it > 0 } ?: 0
}