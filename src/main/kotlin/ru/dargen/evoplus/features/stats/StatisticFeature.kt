package ru.dargen.evoplus.features.stats

import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.evo.data.ComboUpdateEvent
import ru.dargen.evoplus.api.event.evo.data.LevelUpdateEvent
import ru.dargen.evoplus.api.event.interact.BlockBreakEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.item
import ru.dargen.evoplus.api.render.node.postRender
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.features.stats.combo.ComboWidget
import ru.dargen.evoplus.features.stats.level.LevelWidget
import ru.dargen.evoplus.features.stats.pet.PetInfoWidget
import ru.dargen.evoplus.protocol.collector.StatisticCollector.combo
import ru.dargen.evoplus.protocol.collector.StatisticCollector.data
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit
import kotlin.math.max

object StatisticFeature : Feature("statistic", "Статистика", Items.PAPER) {

    val ActivePetsWidget by widgets.widget("Активные питомцы", "active-pets", widget = PetInfoWidget)

    private val ComboTimerPattern =
        "Комбо закончится через (\\d+) секунд\\. Продолжите копать, чтобы не потерять его\\.".toRegex()

    val ComboCounterWidget by widgets.widget("Счетчик комбо", "combo-counter", widget = ComboWidget)
    val ComboProgressBarEnabled by settings.boolean("Шкала прогресса комбо") on {
        ComboWidget.ProgressBar.enabled = it
    }

    val LevelRequireWidget by widgets.widget("Требования на уровень", "level-require", widget = LevelWidget)
    val LevelProgressBarEnabled by settings.boolean("Шкала прогресса уровня") on {
        LevelWidget.ProgressBar.enabled = it
    }
    val NotifyCompleteLevelRequire by settings.boolean("Уведомлять при выполнении требований", true)

    var BlocksCount = 0
        set(value) {
            field = value
            BlocksCounterText.text = "${max(data.blocks - field, 0)}"
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
    val ResetBlocksCounter =
        screen.baseElement("Сбросить счетчик блоков") { button("Сбросить") { on { BlocksCount = data.blocks } } }

    var BlocksPerSecondCounter = mutableMapOf<BlockPos, Long>()
    val BlocksPerSecondWidget by widgets.widget("Счетчик блоков за секунду", "blocks-per-second-counter") {
        origin = Relative.LeftCenter
        align = v3(.87, .60)
        +hbox {
            space = .0
            indent = v3()

            +text("0") {
                isShadowed = true
                postRender { _, _ ->
                    BlocksPerSecondCounter.entries.removeIf { currentMillis - it.value > 1000 }
                    text = "${BlocksPerSecondCounter.size}"
                }
            }
            +item(itemStack(Items.WOODEN_PICKAXE)) {
                scale = v3(.7, .7, .7)
            }
        }
    }

    init {
        ComboWidget.ProgressBar.enabled = ComboProgressBarEnabled
        LevelWidget.ProgressBar.enabled = LevelProgressBarEnabled

        scheduleEvery(unit = TimeUnit.SECONDS) {
            PetInfoWidget.update()
            ComboWidget.update(combo)
        }

        on<BlockBreakEvent> {
            BlocksPerSecondCounter[blockPos] = currentMillis
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
            LevelWidget.update(data)

            if (NotifyCompleteLevelRequire && level.isCompleted && !previousLevel.isCompleted) {
                Notifies.showText("§aВы можете повысить уровень!")
            }

            if (BlocksCount == 0) BlocksCount = data.blocks
            BlocksCounterText.text = "${max(data.blocks - BlocksCount, 0)}"
        }
    }

}