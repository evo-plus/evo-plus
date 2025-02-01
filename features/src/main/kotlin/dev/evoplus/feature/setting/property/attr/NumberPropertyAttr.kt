package dev.evoplus.feature.setting.property.attr

/**
 * For [PropertyType.Slider] and [PropertyType.Number]
 */
data class NumberPropertyAttr(val range: IntProgression) {
    constructor(min: Int, max: Int, increment: Int) : this(min..max step increment)

    val min get() = range.first
    val max get() = range.last
    val step get() = range.step

}