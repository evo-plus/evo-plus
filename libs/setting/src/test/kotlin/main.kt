package dev.evoplus.setting.example

import gg.essential.universal.UScreen
import gg.essential.universal.standalone.runUniversalCraft

fun main() = runUniversalCraft("Test", 854, 480) { window ->
    UScreen.displayScreen(TestSettings.gui())
    window.renderScreenUntilClosed()
}
