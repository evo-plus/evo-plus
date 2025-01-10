package ru.dargen.evoplus.service.visual

import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.util.rest.extract
import ru.dargen.evoplus.util.rest.request
import java.io.InputStreamReader
import java.net.http.HttpResponse.BodyHandlers
import java.util.*
import java.util.concurrent.TimeUnit

object NamePlaceholders {

    private const val PREFIX_PROPERTIES_URL =
        "https://raw.githubusercontent.com/asyncdargen/evo-plus/kotlin/data/prefix.properties"

    val Placeholders = Properties()
//    var BakedPattern = "^$".toRegex()
//        private set
//    val ReplaceCache: MutableMap<String, String> = hashMapOf()

    init {
        scheduleEvery(0, 1, unit = TimeUnit.MINUTES) {
            request(PREFIX_PROPERTIES_URL, handler = BodyHandlers.ofInputStream())
                .extract()
                .thenAccept { Placeholders.load(InputStreamReader(it, Charsets.UTF_8)) }
//            ReplaceCache.clear()
        //            bakePattern()
        }
    }

    fun apply(text: String) = if (text in Placeholders) Placeholders.getProperty(text).replace("%text%", text) else text

//    //TODO: блять доделать чтобы не было километровых регулярок потом
//    fun replace(text: String) = text.replace(BakedPattern) {
//        val value = it.value
//        ReplaceCache.getOrPut(value) { Replacer.getProperty(value).replace("%text%", value) }
//    }

//    private fun bakePattern() {
//        BakedPattern = "(${Replacer.keys().asSequence().joinToString("|")})".toRegex()
//    }

}