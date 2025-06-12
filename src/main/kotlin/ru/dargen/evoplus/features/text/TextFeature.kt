package ru.dargen.evoplus.features.text

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.chat.ChatSendEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.widget.widget
import ru.dargen.evoplus.features.text.market.MarketChatTimerWidget
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.uncolored

object TextFeature : Feature("text", "Текст") {

    var MarketChatTimerDelay = 3

    var NoSpam = false
    var CopyMessages = true
    var EmojiMenu = true

    var KeepHistory = false
    var LongerChat = 0

    override fun CategoryBuilder.setup() {
        slider(
            ::MarketChatTimerDelay,
            "Задержка торгового чата",
            "Время задержки между сообщениями в торговом чате",
            range = 3..5
        )
        widget("market-chat-timer", "Таймер торгового чата", MarketChatTimerWidget, false)

        subcategory("chat-messages", "Сообщения в чате") {
            switch(::NoSpam, "Отключение спам-сообщений", "Отключает ненужные спам-сообщения в чате")
            switch(::CopyMessages, "Копировние сообщений", "Позволяет копировать сообщения из чата правой кнопкой мыши")
            switch(::EmojiMenu, "Меню эмоджи", "Отображать список эмоджи при открытии чата")
        }

        subcategory("chat-history", "История чата") {
            switch(
                ::KeepHistory,
                "История чата после перезахода",
                "Сохраняет всю историю вашего чата после перезахода на сервер"
            )
            slider(
                ::LongerChat,
                "История чата",
                "Увеличивает максимальное количество сообщений в истории чата",
                range = 0..15000 step 100
            )
        }
    }

    override fun initialize() {
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

//        on<StringRenderEvent> {
//            if (!ReplaceUniqueUsers) return@on

//            text = text?.let(ReplacerParser::replace)

//            this.text = Replacer
//                .mapKeys { it.key.cast<String>() }
//                .mapValues { it.value.cast<String>() }
//                .filterKeys { it in text }
//                .entries
//                .fold(text) { currentText, (key, value) -> currentText.replace(key, value).replace("%text%", key) }
//        }
    }

    fun isLongerChat(): Boolean {
        return LongerChat != 0
    }
}
