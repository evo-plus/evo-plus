package ru.dargen.evoplus.features.clicker

enum class ClickerMode(val display: String) {
    
    CLICK("Удар"),
    HOLD("Удержание"),
    ;
    
    override fun toString() = display
}