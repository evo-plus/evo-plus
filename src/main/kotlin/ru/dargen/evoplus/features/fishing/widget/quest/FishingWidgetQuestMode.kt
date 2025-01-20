package ru.dargen.evoplus.features.fishing.widget.quest

enum class FishingWidgetQuestMode(val displayName: String) {
    
    ALL("Везде"),
    NORMAL("Обычный Мир"),
    NETHER("Адский Мир");
    
    fun isVisible(type: String) = this === ALL || type == name
    
    override fun toString() = displayName
    
}