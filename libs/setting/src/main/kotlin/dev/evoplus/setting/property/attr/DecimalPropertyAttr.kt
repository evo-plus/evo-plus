package dev.evoplus.setting.property.attr

typealias FloatRange = ClosedFloatingPointRange<Float>

/**
 * For [PropertyType.DecimalSlider]
 */
data class DecimalPropertyAttr(val range: FloatRange, val decimals: Int) {

    val min get() = range.start
    val max get() = range.endInclusive

}