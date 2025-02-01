package ru.dargen.evoplus.feature.widget

import ru.dargen.evoplus.feature.setting.group.SettingGroup
import ru.dargen.evoplus.render.node.Node

class WidgetGroup : SettingGroup("widgets", "Виджеты") {

    fun widget(
        name: String, id: String = "",
        enabled: Boolean = true,
        widget: Node.() -> Unit
    ) = lazy { name }//widget(Widget(id, name, widget), enabled)

    fun widget(widget: Widget, enabled: Boolean = true): Widget {
        widget.enabled = enabled
        return setting(widget)
    }

}