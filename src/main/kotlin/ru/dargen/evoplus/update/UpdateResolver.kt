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
        get() = !EvoPlus.DevEnvironment && latestVersion?.name?.versionsTriple?.isGreaterThan(EvoPlus.Version.versionsTriple) == true

    init {
        fetchLatestVersion()
    }

    private fun fetchLatestVersion() = catch("Error while fetching latest mod version") {
        Modrinth.fetchShortVersions().firstOrNull { it.channel == Channel }?.also { latestVersion = it }
    }

    fun schedule() {
        scheduleEvery(1, 1, unit = TimeUnit.MINUTES) {
            fetchLatestVersion()

            if (Client?.inGameHud != null && isOutdated) {
                NotifyWidget.showText(
                    "Обнаружена новая версия EvoPlus - ${latestVersion?.friendlyName}",
                    "Нажмите, чтобы обновиться.",
                    delay = 15.0,
                    action = { Updater.openUpdateScreenIfNeed() }
                )
                if (isTooOutdated)
                    Updater.openUpdateScreen()
            }
        }

    }

    val isTooOutdated
        get() = isOutdated && EvoPlus.Version.versionsTriple.let { current ->
            val latest = (latestVersion ?: fetchLatestVersion() ?: return false).name.versionsTriple

            when {
                latest.first > current.first -> true
                latest.first < current.first -> false
                latest.second > current.second -> true
                latest.second < current.second -> false
                latest.third - current.third >= 2 -> true
                else -> false
            }
        }

    private val String.versionsTriple: Triple<Int, Int, Int>
        get() = replace("(-dev|-hj)".toRegex(), "")
            .split(".")
            .map { it.toIntOrNull() ?: 0 }
            .let { parts ->
                Triple(
                    parts.getOrElse(0) { 0 },
                    parts.getOrElse(1) { 0 },
                    parts.getOrElse(2) { 0 }
                )
            }

    private fun Triple<Int, Int, Int>.isGreaterThan(other: Triple<Int, Int, Int>): Boolean {
        return when {
            this.first != other.first -> this.first > other.first
            this.second != other.second -> this.second > other.second
            else -> this.third > other.third
        }
    }

}