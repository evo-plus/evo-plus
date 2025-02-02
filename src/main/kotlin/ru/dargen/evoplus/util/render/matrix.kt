@file:Suppress("NOTHING_TO_INLINE")

package ru.dargen.evoplus.util.render

import net.minecraft.client.util.math.MatrixStack

inline fun MatrixStack.normalize3DScale() = scale(-0.025F, -0.025F, 0.025F)

inline fun MatrixStack.push(block: MatrixStack.() -> Unit) {
    try {
        push()
        block()
    } finally {
        pop()
    }
}