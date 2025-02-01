package ru.dargen.evoplus.feature

import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.event.game.MinecraftLoadedEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.config.FeatureConfig
import ru.dargen.evoplus.keybind.Keybinds.MenuKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

data object Features {

    val Configs = mutableListOf<FeatureConfig<*>>()

    val Features = mutableListOf<Feature>()

    init {
        on<MinecraftLoadedEvent> { Features.forEach { it.initialize() } }

        Runtime.getRuntime().addShutdownHook(thread(false) { saveConfigs() })
        scheduleEvery(unit = TimeUnit.MINUTES) { saveConfigs() }

        MenuKey.on { FeaturesSettings.open() }
    }

    fun setup(block: MutableList<Feature>.() -> Unit) {
        mutableListOf<Feature>().apply(block).forEach {
            Features.add(it)
            it.setupInternal(null)
        }

        FeaturesSettings.initialize()

        Features.forEach(Feature::preInitialize)
    }

    fun saveConfigs() = Configs.forEach(FeatureConfig<*>::save)

    inline fun <reified T> config(name: String, value: T) =
        FeatureConfig(name, object : TypeToken<T>() {}, value).apply {
            load()
            Configs.add(this)
        }
}
