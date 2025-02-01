package dev.evoplus.feature.setting.property

import com.google.gson.JsonElement
import dev.evoplus.feature.setting.Settings
import dev.evoplus.feature.setting.property.data.PropertyItem
import dev.evoplus.feature.setting.property.serializer.PropertySerializer

fun <P : Property<*, *>> P.subscription() = apply { meta.subscription = true }

data class Property<V, A>(
    val id: String, val type: PropertyType<V, A>,
    val meta: PropertyMeta, val attr: A,
    val value: PropertyValue<V>, val serializer: PropertySerializer<V>,

    val observer: (V) -> Unit, val observeInit: Boolean = false,

    private val instance: Settings,
) {

    internal fun createItem() = PropertyItem(this)

    internal fun createComponent() = type.createComponent(value, attr)

    internal fun callObserver(init: Boolean = false) {
        if (observeInit || !init) {
            observer(value.getValue())
        }
    }

    internal fun serialize() = serializer.serialize(value.getValue())

    internal fun deserialize(json: JsonElement) = serializer.deserialize(json).onSuccess(value::setValue)

    fun setValue(value: V?) {
        if (value == null) {
            println("null value assigned to property $id")
            return
        }

        this.value.setValue(value)
        callObserver()

        instance.markDirty()
    }

}