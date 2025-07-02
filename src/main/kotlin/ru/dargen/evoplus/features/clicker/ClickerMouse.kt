package ru.dargen.evoplus.features.clicker

import ru.dargen.evoplus.util.minecraft.ClientExtension

enum class ClickerMouse(val display: String) {
    
    LEFT("ЛКМ") {
        override fun invoke() {
            ClientExtension.`evo_plus$leftClick`()
        }
    },
    RIGHT("ПКМ") {
        override fun invoke() {
            ClientExtension.`evo_plus$rightClick`()
        }
    };

    abstract operator fun invoke()

    override fun toString() = display
}