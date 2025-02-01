package ru.dargen.evoplus.features.misc.resource

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent
import ru.dargen.evoplus.event.resourcepack.ResourcePackRequestEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider
import ru.dargen.evoplus.resource.diamondworld.DiamondWorldPackProvider
import ru.dargen.evoplus.util.minecraft.Client

object ResourcePackFeature : Feature(name = "Пакеты ресурсов") {

    private val Downloader =
        ResourcePackDownloader("https://files.diamondworld.pro/resourcePacks/DiamondWorld_Latest.zip")

    private var disableAutoLoad = false
    private var preload = false

    override fun CategoryBuilder.setup() {
        switch(
            ::disableAutoLoad,
            "Отключение автозагрузки РП",
            "Отключает загрузку РП DiamondWorld"
        )
        switch(
            ::preload,
            "Пред-загрузка РП",
            "Загружает РП DiamondWorld при запуске",
            observeInit = false
        ) { Client?.reloadResources() }
        button(
            "Проверить РП",
            "Проверяет наличие обновлений РП и загружает их",
            text = "Проверить"
        ) { if (preload) checkVersion() }
    }

    override fun preInitialize() {
        on<ResourcePackProvidersEvent> {
            providers.add(DiamondWorldPackProvider(Downloader, ::preload))
            providers.add(EvoPlusPackProvider())
        }
    }

    override fun initialize() {
        on<ResourcePackRequestEvent> {
            if (disableAutoLoad) {
                responseAccepted = true
                cancel()
            }
        }
    }

    private fun checkVersion() = Downloader.download().thenAccept {
        if (Downloader.supplied) NotifyWidget.showText(
            "РП DiamondWorld не требует обновления",
            delay = 5.0
        ) else NotifyWidget.showText(
            "РП DiamondWorld обновлен",
            "Для перезагрузки нажмите",
            delay = 20.0
        ) { Client?.reloadResources() }
    }

}