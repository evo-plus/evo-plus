package ru.dargen.evoplus.features.stats

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.evo.data.ComboUpdateEvent
import ru.dargen.evoplus.event.evo.data.LevelUpdateEvent
import ru.dargen.evoplus.event.interact.BlockBreakEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.stats.combo.ComboWidget
import ru.dargen.evoplus.features.stats.level.LevelWidget
import ru.dargen.evoplus.features.stats.pet.PetInfoWidget
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector.combo
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector.economic
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.postRender
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit
import kotlin.math.max

object StatisticFeature : Feature("statistic", "Статистика", Items.PAPER) {

    private val ComboTimerPattern = "Комбо закончится через (\\d+) секунд\\. Продолжите копать, чтобы не потерять его\\.".toRegex()

    val LevelRequireWidget by widgets.widget("Требования на уровень", "level-require", widget = LevelWidget)
    val ComboCounterWidget by widgets.widget("Счетчик комбо", "combo-counter", widget = ComboWidget)
    val ActivePetsWidget by widgets.widget("Активные питомцы", "active-pets", widget = PetInfoWidget)

    var BlocksCount = 0
        set(value) {
            field = value
            BlocksCounterText.text = "${max(economic.blocks - field, 0)}"
        }
    val BlocksCounterText = text("0") { isShadowed = true }
    val BlocksCounterWidget by widgets.widget("Счетчик блоков", "block-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .54)
        +hbox {
            space = .0
            indent = v3()

            +BlocksCounterText
            +item(itemStack(Items.DIAMOND_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }

    var BlocksPerSecondCounter = mutableListOf<Long>()
    val BlocksPerSecondWidget by widgets.widget("Счетчик блоков за секунду", "blocks-per-second-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .60)
        +hbox {
            space = .0
            indent = v3()

            +text("0") {
                isShadowed = true
                postRender { _, _ ->
                    BlocksPerSecondCounter.removeIf { currentMillis - it > 1000 }
                    text = "${BlocksPerSecondCounter.size}"
                }
            }
            +item(itemStack(Items.WOODEN_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }

    var NotifyCompleteLevelRequire = true
    var LevelProgressBarEnabled = true
    var ComboProgressBarEnabled = true

    override fun CategoryBuilder.setup() {
        subcategory("statistic-widget", "Виджеты") {
            switch(::LevelProgressBarEnabled, "Шкала прогресса уровня", "Включение/отключение виджета шкалы прогресса уровня")
            switch(::ComboProgressBarEnabled, "Шкала прогресса комбо", "Включение/отключение виджета шкалы прогресса комбо")
        }
        switch(::NotifyCompleteLevelRequire, "Уведомлять при выполнении требований", "Включение/отключение уведомлений при выполнении требований на уровень")
        button("Сбросить счетчик блоков") { BlocksCount = economic.blocks }
    }

    override fun initialize() {
        scheduleEvery(unit = TimeUnit.SECONDS) {
            PetInfoWidget.update()
            ComboWidget.update(combo)
        }

        on<BlockBreakEvent> {
            BlocksPerSecondCounter.add(currentMillis)
        }

        on<ChatReceiveEvent> {
            ComboTimerPattern.find(text.uncolored())?.let {
                val remain = it.groupValues[1].toIntOrNull() ?: return@on
                combo.remain = remain.toLong()
                ComboWidget.update(combo)
            }
        }

        on<ComboUpdateEvent> {
            ComboWidget.update(combo)
        }

        on<LevelUpdateEvent> {
            LevelWidget.update(economic)

            if (NotifyCompleteLevelRequire && level.isCompleted && !previousLevel.isCompleted) {
                NotifyWidget.showText("§aВы можете повысить уровень!")
            }

            if (BlocksCount == 0) BlocksCount = economic.blocks
            BlocksCounterText.text = "${max(economic.blocks - BlocksCount, 0)}"
        }
    }

}