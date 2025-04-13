package dev.evoplus.feature.setting.property.attr

import dev.evoplus.feature.setting.property.value.WidgetData

data class WidgetPropertyAttr(val widget: WidgetAccessor) {

    interface WidgetAccessor {

        val id: String

        fun update(data: WidgetData)

        fun snapshot(data: WidgetData)

        fun snapshot(): WidgetData {
            return WidgetData().apply(this::snapshot)
        }

    }

}