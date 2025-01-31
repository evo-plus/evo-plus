package ru.dargen.evoplus.feature.settings

import dev.evoplus.setting.Settings
import kotlin.reflect.KMutableProperty0

//class EnumSelectorPropertyInfo : PropertyInfo() {
//
//    override fun createSettingComponent(initialValue: Any?): SettingComponent {
//        val entries = initialValue!!.javaClass.enumConstants
//        return SelectorComponent(entries.indexOf(initialValue), entries.map { it.toString() })
//    }
//
//}
//
////TODO: make better
//class EnumPropertyBridge<E : Enum<E>>(private val klass: Class<E>, private val delegate: KMutableProperty0<E>) {
//
//    @Suppress("UNCHECKED_CAST")
//    var property: Any
//        set(value) = when (value) {
//            is Int ->    delegate.set(klass.enumConstants[value])
//            is String -> delegate.set(java.lang.Enum.valueOf(klass, value))
//            is Enum<*> ->     delegate.set(value as E)
//            else -> throw IllegalArgumentException("Invalid value type")
//        }
//        get() = delegate.get()
//
//}

inline fun <reified E : Enum<E>> Settings.CategoryBuilder.enumSelector(
    property: KMutableProperty0<E>,
    name: String, description: String = "",
    triggerOnInitialization: Boolean = true,
    hidden: Boolean = false,
    crossinline action: (E) -> Unit = {},
) = Unit/*custom(
    EnumPropertyBridge(E::class.java, property)::property,
    name = name,
    description = description,
    hidden = hidden,
    triggerActionOnInitialization = triggerOnInitialization,
    customPropertyInfo = EnumSelectorPropertyInfo::class
) { action(E::class.java.enumConstants[it as? Int ?: 0]) }*/