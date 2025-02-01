package ru.dargen.evoplus.util

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

fun Path.createDirectoriesIfNotExists() = apply {
    if (!exists()) createDirectories()
}