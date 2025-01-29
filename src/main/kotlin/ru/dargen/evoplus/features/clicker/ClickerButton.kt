package ru.dargen.evoplus.features.clicker

import ru.dargen.evoplus.util.minecraft.ClientExtension

enum class ClickerButton(val display: String, val click: () -> Unit) {
    
    LEFT("ЛКМ", ClientExtension::leftClick),
    RIGHT("ПКМ", ClientExtension::rightClick);

    override fun toString() = display
}