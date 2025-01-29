package ru.dargen.evoplus.features.misc

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.game.GameEvent
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.evo.EvoJoinEvent
import ru.dargen.evoplus.event.evo.data.GameEventChangeEvent
import ru.dargen.evoplus.event.game.PostTickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackRequestEvent
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.features.misc.selector.FastSelectorScreen
import ru.dargen.evoplus.features.misc.selector.FastSelectorSetting
import ru.dargen.evoplus.features.misc.selector.FastSelectorSetting.provideDelegate
import ru.dargen.evoplus.keybind.Keybinds
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.schedule
import ru.dargen.evoplus.util.minecraft.CurrentScreen
import ru.dargen.evoplus.util.minecraft.Player
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.TimeUnit


object MiscFeature : ru.dargen.evoplus.feature.Feature("misc", "Прочее", Items.REPEATER) {

    private val BoosterMessagePattern = "^[\\w\\s]+ активировал глобальный бустер".toRegex()
    
    val NotifiesWidget by widgets.widget("Уведомления", "notifies-widget", widget = NotifyWidget)

    val FastSelector by settings.boolean("Fast-селектор", true)
    val FastSelectorItems by settings.setting(FastSelectorSetting)

    val AutoSprint by settings.boolean("Авто-спринт", true) on { if (!it) Player?.isSprinting = false }
    val AutoThanks by settings.boolean("Авто /thx", true)

    val ResourcePackLoadDisable by settings.boolean(
        "Отключение загрузки РП DiamondWorld",
        false
    )
    val ShowServerInTab by settings.boolean(
        "Показывать текущий сервер в табе",
        true
    )

    val CaseNotify by settings.boolean("Уведомления о кейсах", true)
    val LuckyBlockNotify by settings.boolean(
        "Уведомления о лаки-блоках",
        true
    )
    val CollectionNotify by settings.boolean(
        "Уведомления о коллекционках",
        true
    )
    val EventNotify by settings.boolean("Уведомления о эвенте", true)

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
