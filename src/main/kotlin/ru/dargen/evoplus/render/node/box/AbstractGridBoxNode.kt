package ru.dargen.evoplus.render.node.box

import ru.dargen.evoplus.render.animation.property.proxied
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.Vector3

@KotlinOpens
abstract class AbstractGridBoxNode : BoxNode() {

    var dependSizeX = true
    var dependSizeY = true

    var fixChildSize = false

    var childrenRelative by proxied(.0)
    var space by proxied(5.0)
    var indent by proxied(Vector3(5.0))

    abstract override fun recompose()

}