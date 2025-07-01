package ru.dargen.evoplus.features.misc

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import pro.diamondworld.protocol.packet.game.GameEvent
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.evo.EvoJoinEvent
import ru.dargen.evoplus.event.evo.data.GameEventChangeEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent
import ru.dargen.evoplus.feature.widget.widget
import ru.dargen.evoplus.features.misc.discord.DiscordRPCFeature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.misc.selector.FastSelectorScreen
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider
import ru.dargen.evoplus.scheduler.schedule
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit

object MiscFeature : Feature("misc", "Прочее") {

    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()

    var FastSelector = true

    var AutoSprint = true
    var AutoThanks = true

    var CaseNotify = true
    var LuckyBlockNotify = true
    var CollectionNotify = true
    var EventNotify = true

    var ShowServerInTab = true

    override fun CategoryBuilder.setup() {

        subcategory("selector", "Fast-селектор") {
            switch(
                ::FastSelector,
                "Fast-селектор",
                "Включение/отключение открытия Fast-селектора по нажатию на клавишу"
            )
            button(
                "Настройка Fast-селектора",
                "Настройка предметов, которые будут отображаться в Fast-селекторе",
                text = "Настроить"
            ) { FastSelectorScreen.open(true) }
        }

        subcategory("auto", "Автоматические функции") {
            switch(::AutoSprint, "Авто-спринт", "Включение автоматического спринта") {
                if (!it) Player?.isSprinting = false
            }
            switch(::AutoThanks, "Авто /thx", "Включение автоматического выполнения команды /thx")
        }

        subcategory("notify", "Уведомления") {
            widget("notifies-widget", "Уведомления", NotifyWidget)
            switch(::CaseNotify, "Уведомления о кейсах", "Уведомлять о найденных кейсах")
            switch(::LuckyBlockNotify, "Уведомления о лаки-блоках", "Уведомлять о найденных лаки-блоках")
            switch(::CollectionNotify, "Уведомления о коллекционках", "Уведомлять о найденных коллекционных предметах")
            switch(::EventNotify, "Уведомления о эвенте", "Уведомлять о начале эвента")
        }
        switch(::ShowServerInTab, "Текущий сервер", "Показывать текущий сервер и зеркало в табе")
    }

    override fun initialize() {
        Keybinds.FastSelector.on { if (CurrentScreen == null && FastSelector) FastSelectorScreen.open() }

        on<ResourcePackProvidersEvent> {
            providers.add(EvoPlusPackProvider())
        }

        on<PostTickEvent> { Player?.apply { if (AutoSprint && forwardSpeed > 0) isSprinting = true } }
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (BoosterMessagePattern.containsMatchIn(text)) thx()
            if (text.startsWith("Вы нашли")) {
                if (CaseNotify && text.contains("кейс")) NotifyWidget.showText("§6$text")
                if (CollectionNotify && text.contains("коллекционный предмет")) NotifyWidget.showText("§a$text")
                if (LuckyBlockNotify && text.contains("лаки-блок")) NotifyWidget.showText("§e$text")
            }
        }
        on<EvoJoinEvent> { schedule(5, TimeUnit.SECONDS) { thx() } }

        on<GameEventChangeEvent> {
            if (EventNotify && new !== GameEvent.EventType.NONE && (old === GameEvent.EventType.NONE || old !== new)) {
                NotifyWidget.showText("§aТекущее событие", new.getName(), delay = 20.0)
            }
        }
        FastSelectorScreen
    }

    fun thx() {
        if (AutoThanks) {
            sendCommand("thx")
        }
    }

}
