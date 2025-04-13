package dev.evoplus.feature.setting.property.value

import gg.essential.elementa.utils.Vector3f

data class WidgetData(
    var enabled: Boolean = true,
    var align: Vector3f = Vector3f(0f, 0f, 0f),
    var origin: Vector3f = Vector3f(0f, 0f, 0f),
    var scale: Double = 1.0
)
