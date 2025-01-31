package dev.evoplus.setting.property

import dev.evoplus.setting.Settings
import dev.evoplus.setting.property.data.PropertyItem
import kotlin.reflect.KMutableProperty0

data class Property<A>(
    val id: String, val type: PropertyType<A>,
    val meta: PropertyMeta, val attr: A,
    val value: PropertyValue,

    val observer: (Any?) -> Unit, val observeInit: Boolean =  false,

    private val instance: Settings,
) {

    fun createItem() = PropertyItem(this)

    fun createComponent() = type.createComponent(value, attr)

    fun isHidden(): Boolean = false

    fun setValue(value: Any?) {
        if (value == null) {
            println("null value assigned to property $id")
            return
        }

        if (observeInit || this.value.initialized) {
            observer.invoke(value)
        }

        this.value.initialized = true
        this.value.setValue(value)

        instance.markDirty()
    }

}

abstract class PropertyValue {

    abstract val id: String

    var initialized = false
    open val writeDataToFile = true

    inline fun <reified T> getAs() = getValue() as T

    abstract fun getValue(): Any?
    abstract fun setValue(value: Any?)

}

class KPropertyBackedPropertyValue<T>(internal val property: KMutableProperty0<T>) : PropertyValue() {

    override val id get() = property.name

    override fun getValue() = property.get()

    override fun setValue(value: Any?) {
        property.set(value as T)
    }
}

class EmptyPropertyValue : PropertyValue() {
    override val writeDataToFile = false
    override val id = "ignored"

    override fun getValue(): Nothing = throw IllegalStateException()

    override fun setValue(value: Any?): Nothing = throw IllegalStateException()

}