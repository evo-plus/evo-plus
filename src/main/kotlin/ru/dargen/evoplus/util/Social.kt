package ru.dargen.evoplus.util

import net.minecraft.util.Identifier
import ru.dargen.crowbar.Accessors
import ru.dargen.evoplus.resource.texture
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.net.URI

enum class Social(val identifier: Identifier, val website: String) {
    
    DISCORD(texture("icons/gui/discord"), "https://discord.gg/tkuVE3fdKt"),
    MODRINTH(texture("icons/gui/modrinth"), "https://modrinth.com/mod/evoplus"),
    GITHUB(texture("icons/gui/github"), "https://github.com/asyncdargen/evo-plus"),
    ;

    init {
        Accessors.unsafe().openField<Boolean>(GraphicsEnvironment::class.java, "headless").staticValue = false
    }

    fun open() = Desktop.getDesktop().browse(URI(website))

}