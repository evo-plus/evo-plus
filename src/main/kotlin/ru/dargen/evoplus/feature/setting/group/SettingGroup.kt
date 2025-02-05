package ru.dargen.evoplus.feature.setting.group

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.dargen.evoplus.feature.screen.FeatureBaseElement
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeatureElementProvider
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.*
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.box.DropElementsBoxNode
import ru.dargen.evoplus.util.json.isNull
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.selector.Selector

@KotlinOpens
class SettingGroup(id: String, name: String) : Setting<MutableList<Setting<*>>>(id, name), FeatureElement, FeatureElementProvider {

    override var value = mutableListOf<Setting<*>>()

    val elements = mutableSetOf<FeatureElement>()
    val settings get() = value

    override val element = this

    override fun createElement(prompt: FeaturePrompt): Node = DropElementsBoxNode().apply {
        text.text = prompt.highlightPrompt(name)
        dropped = false
        holder.addChildren(elements.filter { it.search(prompt) }.map { it.createElement(prompt) })
    }

    override fun search(prompt: FeaturePrompt) = prompt.shouldPass(name) || elements.any { it.search(prompt) }

    fun element(element: FeatureElement) = elements.add(element)

    fun baseElement(name: String, element: FeatureElement) = element(FeatureBaseElement(name, element))

    fun <S : Setting<*>> setting(setting: S) = setting.apply {
        this@SettingGroup.value.add(this)
        if (setting is FeatureElementProvider) elements.add(setting.element)
    }

    fun boolean(name: String, value: Boolean = false, id: String = "") =
        setting(BooleanSetting(id, name, value))

    fun colorInput(name: String, value: Boolean = false, id: String = "") =
        setting(ColorInputSetting(id, name, value))

    fun <T> selector(
        name: String, selector: Selector<T>,
        id: String = "",
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" },
    ) = setting(SelectorSetting(id, name, selector, nameMapper))

    fun <T> switcher(
        name: String, selector: Selector<T>,
        id: String = "",
        nameMapper: Selector<T>.(T?) -> String = { it?.toString() ?: "null" },
    ) = setting(SwitcherSetting(id, name, selector, nameMapper))

    fun group(name: String, id: String = "", block: SettingGroup.() -> Unit = {}) =
        setting(SettingGroup(id, name).apply(block))

    override fun load(element: JsonElement) {
        if (!element.isJsonObject) return

        value.associateWith { element.asJsonObject[it.id] }
            .filterValues { !it.isNull }
            .forEach { (setting, settingElement) -> setting.load(settingElement) }
    }

    override fun store(): JsonElement {
        val group = JsonObject()

        value.forEach { group.add(it.id, it.store()) }

        return group
    }

}