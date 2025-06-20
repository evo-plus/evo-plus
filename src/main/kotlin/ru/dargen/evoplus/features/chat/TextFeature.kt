package ru.dargen.evoplus.features.chat

import net.minecraft.item.Items
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.chat.ChatSendEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.chat.market.MarketChatTimerWidget
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.uncolored
import ru.dargen.evoplus.util.selector.toSelector

object TextFeature : Feature("text", "Текст", Items.WRITABLE_BOOK) {

    val MarketChatTimer by widgets.widget(
        "Таймер торгового чата",
        "market-chat-timer",
        widget = MarketChatTimerWidget,
        enabled = false
    )
    val MarketChatTimerDelay by settings.selector("Задержка торгового чата", (3..5).toSelector()) { "$it мин." }

    val NoSpam by settings.boolean("Отключение спам-сообщений")
    val CopyMessages by settings.boolean("Копировать сообщение из чата (ПКМ)", true)
    val EmojiMenu by settings.boolean("Меню эмодзи", true)

    val KeepHistory by settings.boolean("Сохранение истории чата после перезахода", false)
    val LongerChat by settings.selector("Увеличение истории чата", (0..1000).toSelector()) { "$it строк" }

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }

        val marketKeys = listOf("$", ";")

        on<ChatSendEvent> {
            if (!Connector.isOnPrisonEvo || marketKeys.any { !text.startsWith(it) } || MarketChatTimerWidget.RemainingTime >= currentMillis) return@on

            val timerMultiplier = if (text.length - 1 >= 256) 2 else 1
            MarketChatTimerWidget.RemainingTime = currentMillis + (MarketChatTimerDelay * 60 * 1000 * timerMultiplier)
        }

    }

    fun isLongerChat(): Boolean {
        return if (LongerChat == 0) false
        else LongerChat > 0
    }
}
