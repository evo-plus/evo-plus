package ru.dargen.evoplus.features.fishing.widget.quest

enum class FishWidgetQuestMode(val displayName: String, val type: String? = null) {

    ALL("Все"),
    NORMAL("Мир", "NORMAL"),
    NETHER("Ад", "NETHER");

    fun isVisible(type: String) = this.type == null || this.type == type

    override fun toString() = displayName

}