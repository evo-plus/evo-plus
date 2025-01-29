package ru.dargen.evoplus.feature.setting

import ru.dargen.evoplus.feature.screen.FeatureBaseElement
import ru.dargen.evoplus.feature.screen.FeatureElementProvider
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector

@KotlinOpens
class SwitcherSetting<T>(
    id: String, name: String, selector: Selector<T>,
    nameMapper: Selector<T>.(T?) -> String = { it.toString() }
) : SelectorSetting<T>(id, name, selector, nameMapper), FeatureElementProvider {

    protected val selectedName get() = selector.nameMapper(selector.safeSelected)
    override val element = FeatureBaseElement(name) {
        button(selectedName) {
            on {
                if (selector.index == selector.size - 1) selector.selectOn(0)
                else selector.shift(1)
                label.text = selectedName
            }
        }
    }

}