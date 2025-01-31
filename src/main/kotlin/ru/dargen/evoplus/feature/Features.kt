package ru.dargen.evoplus.feature

import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.event.game.MinecraftLoadedEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.config.FeatureConfig
import ru.dargen.evoplus.features.alchemy.AlchemyFeature
import ru.dargen.evoplus.features.boss.BossFeature
import ru.dargen.evoplus.features.boss.timer.BossTimerFeature
import ru.dargen.evoplus.features.chat.TextFeature
import ru.dargen.evoplus.features.clan.ClanFeature
import ru.dargen.evoplus.features.clan.ShaftFeature
import ru.dargen.evoplus.features.clicker.AutoClickerFeature
import ru.dargen.evoplus.features.dev.DevFeature
import ru.dargen.evoplus.features.dungeon.DungeonFeature
import ru.dargen.evoplus.features.esp.ESPFeature
import ru.dargen.evoplus.features.fishing.FishingFeature
import ru.dargen.evoplus.features.game.GoldenRushFeature
import ru.dargen.evoplus.features.misc.MiscFeature
import ru.dargen.evoplus.features.misc.RenderFeature
import ru.dargen.evoplus.features.potion.PotionFeature
import ru.dargen.evoplus.features.rune.RuneFeature
import ru.dargen.evoplus.features.share.ShareFeature
import ru.dargen.evoplus.features.staff.StaffFeature
import ru.dargen.evoplus.features.stats.StatisticFeature
import ru.dargen.evoplus.keybind.Keybinds.MenuKey
import ru.dargen.evoplus.keybind.on
import ru.dargen.evoplus.scheduler.scheduleEvery
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

data object Features {

    val Configs = mutableListOf<FeatureConfig<*>>()

    val List = mutableListOf<Feature>()

    init {
        on<MinecraftLoadedEvent> {
            load()

            Runtime.getRuntime().addShutdownHook(thread(false) {
                saveConfigs()
            })
            scheduleEvery(unit = TimeUnit.MINUTES) { saveConfigs()  }
        }

        MenuKey.on { FeaturesSettings.open() }
    }

    fun load() {
        if (EvoPlus.DevEnvironment) {
            DevFeature.register()
        }

        AutoClickerFeature.register()
        ESPFeature.register()
        BossTimerFeature.register()
        BossFeature.register()
        StaffFeature.register()
        DungeonFeature.register()
        RuneFeature.register()
        AlchemyFeature.register()
        PotionFeature.register()
        StatisticFeature.register()
        TextFeature.register()
        FishingFeature.register()
        ClanFeature.register()
        ShaftFeature.register()
        GoldenRushFeature.register()
        RenderFeature.register()
        MiscFeature.register()
        ShareFeature.register()

        FeaturesSettings.initialize()
    }

    fun Feature.register() {
        List.add(this)
        setupInternal(null)
    }


    fun saveConfigs() = Configs.forEach(FeatureConfig<*>::save)

    inline fun <reified T> config(name: String, value: T) =
        FeatureConfig(name, object : TypeToken<T>() {}, value).apply {
            load()
            Configs.add(this)
        }
}
