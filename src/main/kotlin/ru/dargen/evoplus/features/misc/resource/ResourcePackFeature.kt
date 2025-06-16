package ru.dargen.evoplus.features.misc.resource

import net.minecraft.item.Items
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent
import ru.dargen.evoplus.event.resourcepack.ResourcePackRequestEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider
import ru.dargen.evoplus.resource.diamondworld.DiamondWorldPackProvider
import ru.dargen.evoplus.util.minecraft.Client

object ResourcePackFeature : Feature("resource-pack", "Пакеты ресурсов", Items.MAP) {

    private val Downloader =
        ResourcePackDownloader("https://files.diamondworld.pro/resourcePacks/DiamondWorld_Latest.zip")

    val disableAutoLoad by settings.boolean("Отключение автозагрузки РП DiamondWorld")
    val preload by settings.boolean("Пред-загрузка РП DiamondWorld") on { Client?.reloadResources() }
    val checkVersion by settings.boolean("Проверить наличие обновлений РП DiamondWorld") on { if (preload) checkVersion() }

    init {

        on<ResourcePackProvidersEvent> {
            providers.add(DiamondWorldPackProvider(Downloader, ::preload))
            providers.add(EvoPlusPackProvider())
        }

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