package ru.dargen.evoplus.features.clan

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.event.evo.data.ChangeLocationEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.world.ChunkLoadEvent
import ru.dargen.evoplus.event.world.ChunkUnloadEvent
import ru.dargen.evoplus.event.world.WorldPreLoadEvent
import ru.dargen.evoplus.event.world.block.BlockChangeEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.async
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.evo.isBarrel
import ru.dargen.evoplus.util.evo.isDetonatingBarrel
import ru.dargen.evoplus.util.format.nounEndings
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*
import kotlin.math.max

object ShaftFeature : Feature("shaft", "Шахта") {

    var WormNotify = true
    var WormMessage = false
    var WormClanMessage = false
    var BarrelsNotify = true
    var BarrelsMessage = false
    var BarrelsClanMessage = false
    var RaidClanMessage = false

    var RaidShaftLevel = 0
    val RaidBossData = 101218
    val RaidEntityData = intArrayOf(100857, 100880, 100889, RaidBossData)

    var Worms = 0
        set(value) {
            field = value
            WormsText.text = "Червей рядом: §6$value"
        }
    val WormsText = text {
        text = "Червей рядом: §6$Worms"
        isShadowed = true
    }
    val WormsWidget by widgets.widget("Счётчик червей", "worms", enabled = false) {
        origin = Relative.CenterBottom
        align = v3(.5, .9)
        +WormsText
    }

    var Barrels = 0
        set(value) {
            if (field == 0 && value == 1) {
                if (BarrelsClanMessage) sendClanMessage("§8[§e${Connector.server.displayName}§8] §6Обнаружена бочка §8[§e/mine ${PlayerDataCollector.location.level}§8]")
                if (BarrelsNotify) NotifyWidget.showText("§6Обнаружена бочка")
                if (BarrelsMessage) printMessage("§6Обнаружена бочка")
            }

            field = value
            BarrelsText.text = "Бочек рядом: §6$value"
        }
    val BarrelsText = text {
        text = "Бочек рядом: §6$Barrels"
        isShadowed = true
    }
    val BarrelsWidget by widgets.widget("Счётчик бочек", "barrels", enabled = false) {
        origin = Relative.CenterTop
        align = v3(.5, .9)
        +BarrelsText
    }

    override fun CategoryBuilder.setup() {
        switch(::WormNotify, "Уведомление о найденных червях", "Показывает уведомление о найденных червях")
        switch(::WormMessage, "Сообщение о найденных червях", "Показывает сообщение о найденных червях")
        switch(::WormClanMessage, "Сообщение о найденных червях в клан чат с указанием шахты", "Показывает сообщение о найденных червях в клан чат с указанием шахты")
        switch(::BarrelsNotify, "Уведомление о найденных бочках", "Показывает уведомление о найденных бочках")
        switch(::BarrelsMessage, "Сообщение о найденных бочках", "Показывает сообщение о найденных бочках")
        switch(::BarrelsClanMessage, "Сообщение о найденных бочках в клан чат", "Показывает сообщение о найденных бочках в клан чат")
        switch(::RaidClanMessage, "Сообщение о начатом рейде в клан чат с указанием шахты", "Показывает сообщение о начатом рейде в клан чат с указанием шахты")
    }

    override fun initialize() {
        scheduleEvery(period = 10) {
            /*if (!WormsWidget.enabled) */return@scheduleEvery

            WorldEntities
                .filterIsInstance<ArmorStandEntity>()
                .filter { "Червь" in it.name.string }
                .apply {
                    val previousWorms = Worms
                    Worms = size

                    if (previousWorms < size) {
                        val text = "§6Обнаружен${if (size > 1) "о" else ""} $size ${
                            size.nounEndings("червь", "червя", "червей")
                        }"
                        
                        if (WormNotify) NotifyWidget.showText(text)
                        if (WormMessage) printMessage(text)
                        if (WormClanMessage) sendClanMessage("§8[§e${Connector.server.displayName}§8] $text §8[§e/mine ${PlayerDataCollector.location.level}§8]")
                    }
                }
        }

        scheduleEvery(period = 10) {
            if (!RaidClanMessage) return@scheduleEvery

            WorldEntities
                .filterIsInstance<ItemDisplayEntity>()
                .filter { it.itemStack.item == Items.LEATHER_HORSE_ARMOR }
                .mapNotNull { it.itemStack.customModelData }
                .filter { it in RaidEntityData }
                .forEach {
                    val previousRaidShaftLevel = RaidShaftLevel
                    RaidShaftLevel = PlayerDataCollector.location.level

                    if (previousRaidShaftLevel == RaidShaftLevel) return@scheduleEvery

                    val raidBossText = if (it == RaidBossData) "§8[§aСтраж§8] "
                    else ""

                    sendClanMessage("§8[§e${Connector.server.displayName}§8] §3Обнаружена рейдовая шахта $raidBossText§8[§e/mine $RaidShaftLevel§8]")
                }
        }

        on<ChangeLocationEvent> { RaidShaftLevel = 0 }
        on<WorldPreLoadEvent> { Barrels = 0 }

        on<ChunkLoadEvent> {
            //TODO: make more better than async
            async {
                chunk.forEachBlocks { _, blockState ->
                    if (blockState.isBarrel() || blockState.isDetonatingBarrel()) ++Barrels
                }
            }
        }
        on<ChunkUnloadEvent> {
            //TODO: make more better than async
            async {
                chunk.forEachBlocks { _, blockState ->
                    if (blockState.isBarrel() || blockState.isDetonatingBarrel()) Barrels = max(Barrels - 1, 0)
                }
            }
        }

        on<BlockChangeEvent> {
            if (oldState?.isDetonatingBarrel() == false && newState.isBarrel()) {
                ++Barrels
                return@on
            }

            if ((oldState?.isBarrel() == true) && (!newState.isBarrel() && !newState.isDetonatingBarrel())) Barrels = max(Barrels - 1, 0)
        }
    }
}