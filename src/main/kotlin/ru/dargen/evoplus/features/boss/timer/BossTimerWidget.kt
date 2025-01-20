package ru.dargen.evoplus.features.boss.timer

import net.minecraft.client.gui.screen.Screen
import ru.dargen.evoplus.feature.widget.WidgetBase
import ru.dargen.evoplus.feature.widget.isWidgetEditor
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ShortName
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature.ShortTimeFormat
import ru.dargen.evoplus.protocol.collector.ClanInfoCollector
import ru.dargen.evoplus.protocol.registry.BossType
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.leftClick
import ru.dargen.evoplus.render.node.rightClick
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.format.asShortTextTime
import ru.dargen.evoplus.util.format.asTextTime
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.sendCommand
import kotlin.math.absoluteValue

object BossTimerWidget : WidgetBase {

    override val node = vbox {
        space = .0
        indent = v3()
    }

    fun update() {
        node._childrens = BossTimerFeature.ComparedBosses
            .filter { (type, _) -> (!BossTimerFeature.OnlyRaidBosses || type.isRaid) || (!BossTimerFeature.OnlyCapturedBosses && type in ClanInfoCollector.Bosses) }
            .take(BossTimerFeature.BossesCount)
            .associate { (key, value) -> key to (value - currentMillis) }
            .ifEmpty { if (isWidgetEditor) BossType.values.take(5).associateWith { 2000L } else emptyMap() }
            .map { (type, remaining) ->
                hbox {
                    space = 1.0
                    indent = v3()

                    val spawned = remaining < 0
                    val remaining = remaining.absoluteValue

                    +item(type.displayItem) { scale = scale(.7, .7) }
                    +text(
                        "${
                            if (ShortName) type.displayLevel else type.displayName
                        }§8: ${if (spawned) "§cуже " else "§f"}${
                            if (ShortTimeFormat) remaining.asShortTextTime else remaining.asTextTime
                        }"
                    ) { isShadowed = true }

                    leftClick { _, state ->
                        if (isHovered && state && CurrentScreen != null && !isWidgetEditor && BossTimerFeature.WidgetTeleport) {
                            sendCommand("boss ${type.level}")
                            true
                        } else false
                    }
                    rightClick {_, state ->
                        if (isHovered && state && CurrentScreen != null && !isWidgetEditor && Screen.hasShiftDown()) {
                            BossTimerFeature.Bosses.remove(type.id)
                            true
                        } else false
                    }

                    recompose()
                }
            }.toMutableList()
    }

}