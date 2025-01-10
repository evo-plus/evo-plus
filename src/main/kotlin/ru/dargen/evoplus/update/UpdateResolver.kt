package ru.dargen.evoplus.update

import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.api.render.node.leftClick
import ru.dargen.evoplus.api.scheduler.scheduleEvery
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.rest.controller
import java.util.concurrent.TimeUnit

object UpdateResolver {

    private val Modrinth = controller<Modrinth>()

    var latestVersion: Modrinth.VersionInfo? = null
    val isOutdated
        get() = !EvoPlus.DevEnvironment && latestVersion != null && latestVersion?.name != EvoPlus.VersionString

    init {
        fetchLatestVersion()
    }

    private fun fetchLatestVersion() = catch("Error while fetching latest mod version") {
        latestVersion = Modrinth.fetchShortVersion()
    }

    fun schedule() {
        scheduleEvery(5, 5, unit = TimeUnit.MINUTES) {
            fetchLatestVersion()

            if (Client?.inGameHud != null && isOutdated) Notifies.showText(
                "Обнаружена новая версия EvoPlus - ${latestVersion?.friendlyName}",
                "Нажмите, чтобы обновиться.",
                delay = 15.0
            ) {
                leftClick { _, state ->
                    if (isHovered && state) {
                        Updater.openUpdateScreenIfNeed()
                        true
                    } else false
                }
            }
        }

    }

}