package ru.dargen.evoplus.features.misc

import dev.evoplus.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.game.GameEvent
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.evo.EvoJoinEvent
import ru.dargen.evoplus.event.evo.data.GameEventChangeEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackRequestEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.misc.selector.FastSelectorScreen
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.schedule
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit


object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()
    
    val NotifiesWidget by widgets.widget("Уведомления", "notifies-widget", widget = NotifyWidget)

    var FastSelector = true

    var AutoSprint = true
    var AutoThanks = true
    var ResourcePackLoadDisable = false

    var CaseNotify = true
    var LuckyBlockNotify = true
    var CollectionNotify = true
    var EventNotify = true

    var ShowServerInTab = true

    override fun CategoryBuilder.setup() {
        subcategory("selector", "Fast-селектор") {
            switch(::FastSelector, "Fast-селектор", "Включение/отключение открытия Fast-селектора по нажатию на клавишу")
            button("Настройка Fast-селектора", "Настройка предметов, которые будут отображаться в Fast-селекторе", "Настроить") { FastSelectorScreen.open(true) }
        }

        subcategory("auto", "Автоматические функции") {
            switch(::AutoSprint, "Авто-спринт", "Включение автоматического спринта") { if (!it) Player?.isSprinting = false }
            switch(::AutoThanks, "Авто /thx", "Включение автоматического выполнения команды /thx")
            switch(::ResourcePackLoadDisable, "Авто-загрузка РП DiamondWorld", "Отключение автоматической загрузки ресурс-пака DiamondWorld")
        }

        subcategory("notify", "Уведомления") {
            switch(::CaseNotify, "Уведомления о кейсах", "Уведомлять о найденных кейсах")
            switch(::LuckyBlockNotify, "Уведомления о лаки-блоках", "Уведомлять о найденных лаки-блоках")
            switch(::CollectionNotify, "Уведомления о коллекционках", "Уведомлять о найденных коллекционных предметах")
            switch(::EventNotify, "Уведомления о эвенте", "Уведомлять о начале эвента")
        }
        switch(::ShowServerInTab, "Текущий сервер", "Показывать текущий сервер и зеркало в табе")
    }

    init {
        Keybinds.FastSelector.on { if (CurrentScreen == null && FastSelector) FastSelectorScreen.open() }

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
        on<ResourcePackRequestEvent> {
            if (ResourcePackLoadDisable) {
                responseAccepted = true
                cancel()
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
