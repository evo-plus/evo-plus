package dev.evoplus.feature.setting.property

import java.lang.reflect.Type
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.javaType

abstract class PropertyValue<T> {

    abstract val id: String
    abstract val type: Type

    open val serializable = true

    abstract fun getValue(): T
    abstract fun setValue(value: T)

}

class KPropertyValue<T>(internal val property: KMutableProperty0<T>) : PropertyValue<T>() {

    @Suppress("NO_REFLECTION_IN_CLASS_PATH")
    @OptIn(ExperimentalStdlibApi::class)
    override val type = property.returnType.javaType
    override val id get() = property.name.asPropertyName()

    override fun getValue() = property.get()

    override fun setValue(value: T) = property.set(value)

}

fun String.asPropertyName() = foldIndexed("") { index, acc, char ->
    "$acc${(if (char.isUpperCase() && index > 0) "-" else "")}${char.lowercase()}"
}

class EmptyPropertyValue : PropertyValue<Nothing>() {

    override val type get() = throw IllegalStateException()
    override val id by lazy { hashCode().toString() }

    override val serializable = false

    override fun getValue() = throw IllegalStateException()

    override fun setValue(value: Nothing){

    }

}