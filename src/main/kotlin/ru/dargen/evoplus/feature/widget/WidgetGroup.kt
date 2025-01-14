package ru.dargen.evoplus.feature.widget

import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.feature.screen.FeatureScreenElements
import ru.dargen.evoplus.feature.settings.SettingsGroup

class WidgetGroup(screen: FeatureScreenElements) : SettingsGroup("widgets", "Виджеты", screen) {

    fun widget(
        name: String, id: String = "",
        enabled: Boolean = true,
        widget: Node.() -> Unit
    ) = widget(Widget(id, name, widget), enabled)

    fun widget(widget: Widget, enabled: Boolean = true): Widget {
        widget.enabled = enabled
        return setting(widget)
    }

}