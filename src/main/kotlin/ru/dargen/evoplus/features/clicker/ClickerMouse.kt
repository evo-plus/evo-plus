package ru.dargen.evoplus.features.clicker

enum class ClickerMouse(val display: String) {
    
    LEFT("ЛКМ") {
        override fun invoke() {
//            leftClick()
        }
    },
    RIGHT("ПКМ") {
        override fun invoke() {
//            rightClick()
        }
    };

    abstract operator fun invoke()

    override fun toString() = display
}