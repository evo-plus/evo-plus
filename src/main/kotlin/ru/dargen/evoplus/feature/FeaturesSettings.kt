package ru.dargen.evoplus.feature

import dev.evoplus.feature.setting.Settings
import gg.essential.universal.UScreen
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.scheduler.after


object FeaturesSettings : Settings(EvoPlus.Folder.resolve("features.json"), "EvoPlus ${EvoPlus.Version}") {

    private fun displayScreen() = UScreen.displayScreen(gui())

    fun open(after: Int? = null) = after?.let { after(after) { displayScreen() } } ?: displayScreen()

}