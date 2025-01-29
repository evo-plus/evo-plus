package ru.dargen.evoplus.feature.vigilant

import gg.essential.universal.UScreen
import gg.essential.vigilance.Vigilant
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.scheduler.after
import java.io.File

object FeaturesVigilant : Vigilant(File("evo-plus/features.toml"), guiTitle = "EvoPlus ${EvoPlus.Version}") {

    private fun displayScreen() = UScreen.displayScreen(gui())

    fun open(after: Int? = null) = after?.let { after(after) { displayScreen() } } ?: displayScreen()

    fun category(name: String, description: String? = null, setup: CategoryPropertyBuilder.() -> Unit) {
        category(name, setup)
        description?.let { setCategoryDescription(name, description) }
    }

}