package ru.dargen.evoplus

import gg.essential.universal.UScreen
import dev.evoplus.setting.Settings
import ru.dargen.evoplus.scheduler.after
import kotlin.io.path.Path


object FeaturesSettings : Settings(Path("evo-plus/features.toml"), "EvoPlus ${EvoPlus.Version}") {

    private val gui by lazy { gui() }
    private fun displayScreen() = UScreen.displayScreen(gui)

    fun open(after: Int? = null) = after?.let { after(after) { displayScreen() } } ?: displayScreen()

}