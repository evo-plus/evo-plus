package ru.dargen.evoplus.update

import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.rest.controller
import java.util.concurrent.TimeUnit

object UpdateResolver {

    private val Modrinth = controller<Modrinth>()

    var latestVersion: Modrinth.VersionInfo? = null
    val isOutdated
        get() = !EvoPlus.DevEnvironment && latestVersion != null && latestVersion?.name != EvoPlus.Version

    init {
        fetchLatestVersion()
    }

    private fun fetchLatestVersion() = catch("Error while fetching latest mod version") {
        latestVersion = Modrinth.fetchShortVersion()
    }

    fun schedule() {
        scheduleEvery(5, 5, unit = TimeUnit.MINUTES) {
            fetchLatestVersion()
            
            if (Client?.inGameHud != null && isOutdated) NotifyWidget.showText(
                "Обнаружена новая версия EvoPlus - ${latestVersion?.friendlyName}",
                "Нажмите, чтобы обновиться.",
                delay = 15.0,
                action = { Updater.openUpdateScreenIfNeed() }
            )
        }

    }

}