package dev.evoplus.feature.setting.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.evoplus.feature.setting.PropertyGson
import dev.evoplus.feature.setting.Settings
import dev.evoplus.feature.setting.property.Category
import java.nio.file.Path
import java.util.logging.Level
import kotlin.concurrent.fixedRateTimer
import kotlin.io.path.name
import kotlin.io.path.reader
import kotlin.io.path.writer

class SettingsFile(private val file: Path, private val settings: Settings) {

    internal var dirty = false

    internal fun initialize() {

        runCatching {
            read()
        }.onFailure {
            write()
            settings.logger.log(Level.SEVERE, "Failed to read config data from ${file.name}", it)
        }

        fixedRateTimer(period = 30 * 1000, daemon = true) { tryWrite() }
        Runtime.getRuntime().addShutdownHook(Thread { write() })
    }

    fun tryWrite() {
        if (!dirty) return
        write()

        dirty = false
    }

    fun write() {
        val body = json {
            settings.categories.forEach { (id, category) ->
                add(id, category.write())
            }
        }

        file.writer().use {
            PropertyGson.toJson(body, it)
        }
    }

    fun read() {
        val categoriesSections = file.reader().use {
            PropertyGson.fromJson(it, JsonObject::class.java)
        }

        categoriesSections.keySet().forEach { category ->
            runCatching {
                settings.categories[category]?.read(categoriesSections[category].asJsonObject)
            }.onFailure {
                settings.logger.log(Level.SEVERE, "Error while reading category $category", it)
            }
        }
    }

    private fun Category.read(json: JsonObject) {
        json.readEach("properties") { id, element ->
            val property = properties[id]?.takeIf { it.value.serializable } ?: return@readEach

            property.deserialize(element).onFailure {
                settings.logger.log(Level.SEVERE, "Failed to deserialize property ${property.id}: $it")
            }
        }

        json.readEach("categories") { category, element ->
            runCatching {
                categories[category]?.read(element.asJsonObject)
            }.onFailure {
                settings.logger.log(Level.SEVERE, "Error while reading sub-category $id.$category", it)
            }
        }
    }

    //recursive problem....
    private val write0: Category.() -> JsonObject = { write() }

    private fun Category.write() = json {
        if (properties.isNotEmpty()) write("properties") {
            properties.filter { it.value.value.serializable }.forEach { (id, property) ->
                property.serialize().fold(
                    { add(id, it) },
                    { settings.logger.log(Level.SEVERE, "Failed to serialize property $id", it) }
                )
            }
        }

        if (categories.isNotEmpty()) write("categories") {
            categories.forEach { (id, category) ->
                add(id, category.write0())
            }
        }
    }

    private fun JsonObject.readEach(name: String, block: (String, JsonElement) -> Unit) = get(name)?.asJsonObject?.apply {
        keySet().forEach { block(it, get(it)) }
    }

    private fun JsonObject.read(name: String, block: JsonObject.() -> Unit) = get(name)?.asJsonObject?.apply(block)

    private fun JsonObject.write(name: String, block: JsonObject.() -> Unit) = add(name, JsonObject().apply(block))

    private fun json(block: JsonObject.() -> Unit) = JsonObject().apply(block)

}