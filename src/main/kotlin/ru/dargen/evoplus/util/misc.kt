package ru.dargen.evoplus.util

import ru.dargen.evoplus.Logger

fun Throwable.log(message: String) = Logger.error(message, this)

typealias Runnable = () -> Unit

fun Runnable.catching(message: String): Runnable = { catch(message, this) }

fun <T> catch(message: String = "", block: () -> T) =
    runCatching(block).apply { exceptionOrNull()?.log(message) }.getOrNull()

val currentMillis get() = System.currentTimeMillis()
val currentNanos get() = System.nanoTime()