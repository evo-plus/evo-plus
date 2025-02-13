package ru.dargen.evoplus.feature.setting

import com.google.gson.JsonElement
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.rectangle
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.math.v3
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias SettingHandler<T> = (T) -> Unit

@KotlinOpens
abstract class Setting<T>(var id: String, val name: String) : ReadWriteProperty<Any, T> {

    abstract var value: T
    var handler: SettingHandler<T> = {}

    infix fun on(handler: SettingHandler<T>) = apply { this.handler = handler }

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

    operator fun provideDelegate(thisRef: Any, property: KProperty<*>) = apply {
        id = id.ifBlank {
            property.name.foldIndexed("") { index, acc, char ->
                "$acc${(if (char.isUpperCase() && index > 0) "-" else "")}${char.lowercase()}"
            }
        }
    }

    abstract fun load(element: JsonElement)

    abstract fun store(): JsonElement

}