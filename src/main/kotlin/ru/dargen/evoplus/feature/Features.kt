package ru.dargen.evoplus.feature

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.event.game.MinecraftLoadedEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.config.FeatureConfig
import ru.dargen.evoplus.keybind.Keybinds.MenuKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.json.Gson
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText

data object Features {

    val SettingsFile = EvoPlus.Folder.resolve("features-old.json")
    val Configs = mutableListOf<FeatureConfig<*>>()

    val Features = mutableListOf<Feature>()

    init {
        on<MinecraftLoadedEvent> {
            Features.forEach { it.initialize() }
        }

        Runtime.getRuntime().addShutdownHook(thread(false) {
            saveSettings()
            saveConfigs()
        })
        scheduleEvery(unit = TimeUnit.MINUTES) {
            saveSettings()
            saveConfigs()
        }

        MenuKey.on { FeaturesSettings.open() }
    }

    fun setup(block: MutableList<Feature>.() -> Unit) {
        mutableListOf<Feature>().apply(block).forEach {
            Features.add(it)
            it.setupInternal(null)
        }

        FeaturesSettings.initialize()

        Features.forEach(Feature::preInitialize)
loadSettings()
    }

    fun saveSettings() {
        if (SettingsFile.parent?.exists() != true) {
            SettingsFile.parent?.createDirectories()
        }

        catch("Error while loading features settings") {
            val json = JsonObject().apply {
                Features.forEach { add(it.settings.id, it.settings.store()) }
            }

            SettingsFile.writeText(Gson.toJson(json))
        }
    }

    fun loadSettings() {
        if (SettingsFile.exists()) catch("Error while loading features settings") {
            val json = Gson.fromJson(SettingsFile.reader(), JsonObject::class.java)

            Features.associateBy { json.get(it.settings.id)?.asJsonObject }
                .filterKeys { it != null }
                .forEach { (element, feature) -> feature.settings.load(element!!) }
        }
    }

    fun saveConfigs() = Configs.forEach(FeatureConfig<*>::save)

    inline fun <reified T> config(name: String, value: T) =
        FeatureConfig(name, object : TypeToken<T>() {}, value).apply {
            load()
            Configs.add(this)
        }
}
