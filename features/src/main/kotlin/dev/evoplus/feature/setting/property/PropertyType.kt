package dev.evoplus.feature.setting.property

import dev.evoplus.feature.setting.gui.settings.ButtonComponent
import dev.evoplus.feature.setting.gui.settings.CheckboxComponent
import dev.evoplus.feature.setting.gui.settings.ColorComponent
import dev.evoplus.feature.setting.gui.settings.DecimalSliderComponent
import dev.evoplus.feature.setting.gui.settings.NumberComponent
import dev.evoplus.feature.setting.gui.settings.PercentSliderComponent
import dev.evoplus.feature.setting.gui.settings.SelectorComponent
import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.gui.settings.SliderComponent
import dev.evoplus.feature.setting.gui.settings.SwitchComponent
import dev.evoplus.feature.setting.gui.settings.TextComponent
import dev.evoplus.feature.setting.property.attr.ButtonPropertyAttr
import dev.evoplus.feature.setting.property.attr.ColorPropertyAttr
import dev.evoplus.feature.setting.property.attr.DecimalPropertyAttr
import dev.evoplus.feature.setting.property.attr.NumberPropertyAttr
import dev.evoplus.feature.setting.property.attr.SelectorPropertyAttr
import dev.evoplus.feature.setting.property.attr.TextPropertyAttr
import dev.evoplus.feature.setting.property.serializer.ColorPropertySerializer
import dev.evoplus.feature.setting.property.serializer.EmptyPropertySerializer
import dev.evoplus.feature.setting.property.serializer.GsonPropertySerializer
import dev.evoplus.feature.setting.property.serializer.PropertySerializer
import dev.evoplus.feature.setting.property.serializer.TextPropertySerializer
import java.awt.Color

private typealias JColor = Color
private typealias Serializer<T> = PropertySerializer<T>
private typealias Value<T> = PropertyValue<T>

abstract class PropertyType<V, A> {

    data object Switch : NoAttr<Boolean>() {
        override fun createComponent(value: Value<Boolean>): SettingComponent {
            return SwitchComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Boolean>, data: Any?): Serializer<Boolean> {
            return GsonPropertySerializer(Boolean::class.java)
        }
    }

    data object CheckBox : NoAttr<Boolean>() {
        override fun createComponent(value: Value<Boolean>): SettingComponent {
            return CheckboxComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Boolean>, data: Any?): Serializer<Boolean> {
            return GsonPropertySerializer(Boolean::class.java)
        }
    }

    data object Slider : PropertyType<Int, NumberPropertyAttr>() {
        override fun createComponent(value: Value<Int>, attr: NumberPropertyAttr): SettingComponent {
            return SliderComponent(value.getValue(), attr.min, attr.max, attr.step)
        }

        override fun createSerializer(value: Value<Int>, data: NumberPropertyAttr): Serializer<Int> {
            return GsonPropertySerializer(Int::class.java)
        }
    }

    data object Number : PropertyType<Int, NumberPropertyAttr>() {
        override fun createComponent(value: Value<Int>, attr: NumberPropertyAttr): SettingComponent {
            return NumberComponent(value.getValue(), attr.min, attr.max, attr.step)
        }

        override fun createSerializer(value: Value<Int>, data: NumberPropertyAttr): Serializer<Int> {
            return GsonPropertySerializer(Int::class.java)
        }
    }

    data object DecimalSlider : PropertyType<Float, DecimalPropertyAttr>() {
        override fun createComponent(value: Value<Float>, attr: DecimalPropertyAttr): SettingComponent {
            return DecimalSliderComponent(value.getValue(), attr.min, attr.max, attr.decimals)
        }

        override fun createSerializer(value: Value<Float>, data: DecimalPropertyAttr): Serializer<Float> {
            return GsonPropertySerializer(Float::class.java)
        }
    }

    data object PercentSlider : NoAttr<Float>() {
        override fun createComponent(value: Value<Float>): SettingComponent {
            return PercentSliderComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Float>, data: Any?): Serializer<Float> {
            return GsonPropertySerializer(Float::class.java)
        }
    }

    data object Text : PropertyType<String, TextPropertyAttr>() {
        override fun createComponent(value: Value<String>, attr: TextPropertyAttr): SettingComponent {
            return TextComponent(value.getValue(), attr.placeholder, false, attr.protected)
        }

        override fun createSerializer(value: Value<String>, data: TextPropertyAttr): Serializer<String> {
            return TextPropertySerializer(data.protected)
        }
    }

    data object Paragraph : PropertyType<String, TextPropertyAttr>() {
        override fun createComponent(value: Value<String>, attr: TextPropertyAttr): SettingComponent {
            return TextComponent(value.getValue(), attr.placeholder, true, attr.protected)
        }

        override fun createSerializer(value: Value<String>, data: TextPropertyAttr): Serializer<String> {
            return TextPropertySerializer(data.protected)
        }
    }

    data object Color : PropertyType<JColor, ColorPropertyAttr>() {
        override fun createComponent(value: Value<JColor>, attr: ColorPropertyAttr): SettingComponent {
            return ColorComponent(value.getValue(), attr.alpha)
        }

        override fun createSerializer(value: Value<JColor>, data: ColorPropertyAttr): PropertySerializer<JColor> {
            return ColorPropertySerializer(data.alpha)
        }
    }

    data object Selector : PropertyType<Any, SelectorPropertyAttr<*>>() {
        override fun createComponent(value: Value<Any>, attr: SelectorPropertyAttr<*>): SettingComponent {
            @Suppress("UNCHECKED_CAST")
            return SelectorComponent((attr as SelectorPropertyAttr<Any>).indexOf(value.getValue()), attr)
        }

        override fun createSerializer(value: Value<Any>, data: SelectorPropertyAttr<*>): PropertySerializer<Any> {
            return GsonPropertySerializer(value.type)
        }
    }

    data object Button : PropertyType<Nothing, ButtonPropertyAttr>() {
        override fun createComponent(value: Value<Nothing>, attr: ButtonPropertyAttr): SettingComponent {
            return ButtonComponent(attr.text)
        }

        override fun createSerializer(value: Value<Nothing>, data: ButtonPropertyAttr): PropertySerializer<Nothing> {
            return EmptyPropertySerializer
        }
    }

    val name get() = toString()

    abstract fun createComponent(value: Value<V>, data: A): SettingComponent

    abstract fun createSerializer(value: Value<V>, data: A): PropertySerializer<V>

    abstract class NoAttr<V>() : PropertyType<V, Any?>() {

        override fun createComponent(value: Value<V>, data: Any?) = createComponent(value)
        abstract fun createComponent(value: Value<V>): SettingComponent

    }

}
