package ru.dargen.evoplus.features.share

import net.minecraft.item.Items
import ru.dargen.evoplus.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.PasteApi
import ru.dargen.evoplus.util.json.Gson
import ru.dargen.evoplus.util.minecraft.uncolored
import java.util.concurrent.CompletableFuture

object ShareFeature : Feature("share", "Поделиться", Items.SCULK_SENSOR) {

    private val OutgoingSharePattern = "^ЛС \\| Я »(?:| .) \\w+: evoplus:\\w+:\\w+\$".toRegex()
    private val IncomingSharePattern = "^ЛС \\|(?:| .) (\\w+) » Я: evoplus:(\\w+):(\\w+)\$".toRegex()
    private val ClanSharePattern = "^\\[Клан] (?:.+ )?(\\w+) \\[.*]: evoplus:(\\w+):(\\w+)\$".toRegex()

    val shares = mutableMapOf<String, ShareSetting>()

    init {
        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (OutgoingSharePattern.containsMatchIn(text)) cancel()
            else (IncomingSharePattern.find(text) ?: ClanSharePattern.find(text))?.run {
                cancel()

                val (nick, id, key) = destructured

                val share = shares[id] ?: return@on
                if (share.value) {
                    CompletableFuture.supplyAsync { PasteApi.copy(key)!! }.thenAccept { share.decoder(nick, it) }
                }
            }
        }
    }

    inline fun <reified T> createOf(
        id: String, name: String,
        crossinline encoder: (nick: String?) -> T,
        crossinline decoder: (nick: String, data: T) -> Unit,
    ) {
        val type = T::class.java
        create(
            id, name,
            { Gson.toJson(encoder(it)) },
            { nick, data -> decoder(nick, Gson.fromJson<T>(data, type)) }
        )
    }

    fun create(
        id: String, name: String,
        encoder: (nick: String?) -> String,
        decoder: (nick: String, data: String) -> Unit,
    ) {
//        shares[id] = ShareSetting(id, name, encoder, decoder)
//        settings.setting(ShareSetting(id, name, encoder, decoder))
    }

}