package dev.evoplus.feature.setting.property.attr

data class SelectorPropertyAttr<T>(val options: List<T>, val toString: (T) -> String) {

    val optionsNames get() = options.map(toString)

    fun indexOf(value: T) = options.indexOf(value)

    fun valueOf(index: Int) = options[index]

}