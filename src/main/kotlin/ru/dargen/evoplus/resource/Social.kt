package ru.dargen.evoplus.resource

import net.minecraft.util.Identifier
import ru.dargen.crowbar.Accessors
import java.awt.Color
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.net.URI

enum class Social(val identifier: Identifier, val color: Color, val website: String) {

    DISCORD(texture("icons/gui/discord"), Color(0x5865F2), "https://discord.gg/tkuVE3fdKt"),
    MODRINTH(texture("icons/gui/modrinth"), Color(0x1BD96A), "https://modrinth.com/mod/evoplus"),
    GITHUB(texture("icons/gui/github"), Color(0x0), "https://github.com/asyncdargen/evo-plus"),
    ;

    init {
        Accessors.unsafe().openField<Boolean>(GraphicsEnvironment::class.java, "headless").staticValue = false
    }

    fun open() = Desktop.getDesktop().browse(URI(website))

}