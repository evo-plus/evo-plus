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
import kotlin.math.ceil

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

    //    val ReplaceUniqueUsers by settings.boolean("Заменять ники уникальных пользователей EvoPlus", true)
    val ColorInputs = settings.colorInput("Градиент сообщение в чате (Нужен статус)", id = "gradient")

    init {
        Emojis

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (NoSpam && text.startsWith("Игроку")) cancel()
        }

        val formatters = listOf("!", "@")
        val marketKeys = listOf("$", ";")
        val marketWords = listOf(
            "куплю",
            "продам",
            "покупка",
            "продажа",
            "купить",
            "продать",
            "скупаю",
            "покупаю",
            "продаю",
            "скупает",
            "покупает",
            "продает",
            "buy",
            "sell",
            "прадам",
            "пакупка",
            "скуплю",
            "прадаю",
            "куплу",
            "обмен",
            "обменяю"
        )

        on<ChatSendEvent> {
            if (!Connector.isOnPrisonEvo || !ColorInputs.value) return@on
            if (text.startsWith("@") || text.startsWith("\"")) return@on

            val message = text
            val prefix = formatters.find { message.startsWith(it, true) }?.take(1) ?: ""
            val colors = buildColorSetting(ColorInputs.mirroring)
            text = if (marketKeys.any { message.startsWith(it) } || marketWords.any { message.contains(it, true) })
                text else message.replace(prefix, "").buildMessage(prefix, colors)
        }

        on<ChatSendEvent> {
            if (!Connector.isOnPrisonEvo || marketKeys.any { !text.startsWith(it) } || MarketChatTimerWidget.RemainingTime >= currentMillis) return@on

            val timerMultiplier = if (text.length - 1 >= 256) 2 else 1
            MarketChatTimerWidget.RemainingTime = currentMillis + MarketChatTimerDelay * 60 * 1000 * timerMultiplier
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

    private fun String.takeFirstHalf() = take(ceil(length / 2.0).toInt())
    private fun String.takeSecondHalf() = drop(ceil(length / 2.0).toInt())

    private fun String.buildMessage(prefix: String, colors: List<String>): String {
        val mirroring = colors.size == 2
        return if (mirroring) "$prefix${colors[0]}${takeFirstHalf()}${colors[1]}${takeSecondHalf()}"
        else "$prefix${colors[0]}$this"
    }

    private fun buildColorSetting(withMirroring: Boolean) = buildList {
        ColorInputs.inputs.map { it.content }.let {
            val firstColor = it[0].uppercase()
            val secondColor = it[1].uppercase()

            add("[#$firstColor-#$secondColor]")
            if (withMirroring) add("[#$secondColor-#$firstColor]")
        }
    }

    fun isLongerChat(): Boolean {
        return if (LongerChat == 0) false
        else LongerChat > 0
    }
}
