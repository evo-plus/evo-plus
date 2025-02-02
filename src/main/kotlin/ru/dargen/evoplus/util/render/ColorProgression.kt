package ru.dargen.evoplus.util.render

import ru.dargen.evoplus.util.math.fix
import ru.dargen.evoplus.util.math.progressTo
import java.awt.Color

class ColorProgression(val src: Color, val dst: Color) {

    fun map(first: (Color) -> Color, second: (Color) -> Color) = ColorProgression(first(src), second(dst))

    fun map(mapper: (Color) -> Color) = map(mapper, mapper)

    fun at(progress: Double) = src.apply {
        val progress = progress.fix(.0, 1.0)
        Color(
            red.progressTo(dst.red, progress).fixCC(),
            green.progressTo(dst.green, progress).fixCC(),
            blue.progressTo(dst.blue, progress).fixCC(),
            alpha.progressTo(dst.alpha, progress).fixCC()
        )
    }

}