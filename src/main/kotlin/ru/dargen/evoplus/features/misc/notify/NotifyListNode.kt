package ru.dargen.evoplus.features.misc.notify

import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.AbstractGridBoxNode
import ru.dargen.evoplus.util.math.v3

class NotifyListNode : AbstractGridBoxNode() {

    var modifications = 0
    var currentModification = 0

    override fun recompose() {
        if (modifications == currentModification) return
        currentModification = modifications

        var translateY = indent.y
        var maxX = .0

        val children = children.filter { it !in nonComposingChildren }

        children.forEachIndexed { index, node ->
            if (index > 0) {
                translateY += space
            }

            node.animate("recompose", .15) {
                node.align = v3(childrenRelative)
                node.origin = v3(childrenRelative)
                node.position = v3(indent.x, translateY, .0)
            }

            if (node.size.x * node.scale.x > maxX) {
                maxX = node.size.x * node.scale.x
            }

            translateY += node.size.y * node.scale.y
        }

        if (dependSizeX) {
            size.x = if (children.isEmpty()) .0 else maxX + indent.x * 2
        }
        if (dependSizeY) {
            size.y = if (children.isEmpty()) .0 else translateY + indent.y
        }

        if (fixChildSize) {
            enabledChildren.forEach {
                if (it !in nonComposingChildren) {
                    it.size.x = (if (dependSizeX) maxX else size.x - indent.x * 2) / it.scale.x
                }
            }
        }
    }

    override fun addChildren(children: Collection<Node>) {
        super.addChildren(children)
        modifications++
    }

    override fun removeChildren(children: Collection<Node>) {
        super.removeChildren(children)
        modifications++
    }

}