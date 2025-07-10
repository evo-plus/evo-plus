package ru.dargen.evoplus.features.clicker

import ru.dargen.evoplus.util.minecraft.leftClick
import ru.dargen.evoplus.util.minecraft.rightClick

enum class ClickerMouse(val display: String) {
    
    LEFT("ЛКМ") {
        override fun invoke() {
            leftClick()
        }
    },
    RIGHT("ПКМ") {
        override fun invoke() {
            rightClick()
        }
    };

    abstract operator fun invoke()

    override fun toString() = display
}