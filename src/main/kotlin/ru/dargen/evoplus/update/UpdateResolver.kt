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
    private val Channel = if (EvoPlus.Alpha) "alpha" else "release"
    var latestVersion: Modrinth.VersionInfo? = null
    val isOutdated
        get() = !EvoPlus.DevEnvironment && latestVersion != null && latestVersion?.name != EvoPlus.Version

    init {
        fetchLatestVersion()
    }

    private fun fetchLatestVersion() = catch("Error while fetching latest mod version") {
        Modrinth.fetchShortVersions().firstOrNull { it.channel == Channel }?.also { latestVersion = it }
    }

    fun schedule() {
        scheduleEvery(1, 1, unit = TimeUnit.MINUTES) {
            fetchLatestVersion()

            if (Client?.inGameHud != null && isOutdated){
                NotifyWidget.showText(
                    "Обнаружена новая версия EvoPlus - ${latestVersion?.friendlyName}",
                    "Нажмите, чтобы обновиться.",
                    delay = 15.0,
                    action = { Updater.openUpdateScreenIfNeed() }
                )
                if (isTooOutdated) {
                    Updater.openUpdateScreen()
                }
            }
        }

    }

    val isTooOutdated
        get() = isOutdated && EvoPlus.Version.versions.let { current ->
            val latest = (latestVersion ?: fetchLatestVersion() ?: return false).name.versions
            latest[0] != current[0] || latest[1] != current[1] || latest[2] - current[2] >= 2
        }

    private val String.versions get() = replace("(-dev|-hj)".toRegex(), "").split(".").map(String::toInt)

}