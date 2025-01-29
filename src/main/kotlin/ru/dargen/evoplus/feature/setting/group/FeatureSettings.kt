package ru.dargen.evoplus.feature.setting.group

import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.node.scroll.vScrollView
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class FeatureSettings(id: String, name: String) : SettingGroup(id, name) {

    override fun createElement(prompt: FeaturePrompt) = vScrollView {
        box.color = Colors.TransparentBlack

        addElements(this@FeatureSettings.elements.filter { it.search(prompt) }.map { it.createElement(prompt) })
    }


}