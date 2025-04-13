package dev.evoplus.feature.setting.property

import dev.evoplus.feature.setting.gui.settings.ButtonComponent
import dev.evoplus.feature.setting.gui.settings.NumberComponent
import dev.evoplus.feature.setting.gui.settings.SelectorComponent
import dev.evoplus.feature.setting.gui.settings.SettingComponent
import dev.evoplus.feature.setting.gui.settings.color.ColorComponent
import dev.evoplus.feature.setting.gui.settings.color.SwitchColorComponent
import dev.evoplus.feature.setting.gui.settings.input.BindComponent
import dev.evoplus.feature.setting.gui.settings.input.TextComponent
import dev.evoplus.feature.setting.gui.settings.slider.DecimalSliderComponent
import dev.evoplus.feature.setting.gui.settings.slider.PercentSliderComponent
import dev.evoplus.feature.setting.gui.settings.slider.SliderComponent
import dev.evoplus.feature.setting.gui.settings.toggle.CheckboxComponent
import dev.evoplus.feature.setting.gui.settings.toggle.SwitchComponent
import dev.evoplus.feature.setting.property.attr.*
import dev.evoplus.feature.setting.property.serializer.*
import dev.evoplus.feature.setting.property.value.Bind
import dev.evoplus.feature.setting.property.value.SwitchColor
import dev.evoplus.feature.setting.property.value.WidgetData
import java.awt.Color

private typealias JColor = Color
private typealias Serializer<T> = PropertySerializer<T>
private typealias Value<T> = PropertyValue<T>
private typealias SwitchColorData = SwitchColor
private typealias BindData = Bind

abstract class PropertyType<V, A> {

    data object Switch : NoAttr<Boolean>() {
        override fun createComponent(value: Value<Boolean>): SettingComponent {
            return SwitchComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Boolean>, data: Any?): Serializer<Boolean> {
            return TreePropertySerializer(Boolean::class.java)
        }
    }

    data object CheckBox : NoAttr<Boolean>() {
        override fun createComponent(value: Value<Boolean>): SettingComponent {
            return CheckboxComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Boolean>, data: Any?): Serializer<Boolean> {
            return TreePropertySerializer(Boolean::class.java)
        }
    }

    data object Slider : PropertyType<Int, NumberPropertyAttr>() {
        override fun createComponent(value: Value<Int>, attr: NumberPropertyAttr): SettingComponent {
            return SliderComponent(value.getValue(), attr.min, attr.max, attr.step)
        }

        override fun createSerializer(value: Value<Int>, data: NumberPropertyAttr): Serializer<Int> {
            return TreePropertySerializer(Int::class.java)
        }
    }

    data object Number : PropertyType<Int, NumberPropertyAttr>() {
        override fun createComponent(value: Value<Int>, attr: NumberPropertyAttr): SettingComponent {
            return NumberComponent(value.getValue(), attr.min, attr.max, attr.step)
        }

        override fun createSerializer(value: Value<Int>, data: NumberPropertyAttr): Serializer<Int> {
            return TreePropertySerializer(Int::class.java)
        }
    }

    data object DecimalSlider : PropertyType<Float, DecimalPropertyAttr>() {
        override fun createComponent(value: Value<Float>, attr: DecimalPropertyAttr): SettingComponent {
            return DecimalSliderComponent(value.getValue(), attr.min, attr.max, attr.decimals)
        }

        override fun createSerializer(value: Value<Float>, data: DecimalPropertyAttr): Serializer<Float> {
            return TreePropertySerializer(Float::class.java)
        }
    }

    data object PercentSlider : NoAttr<Float>() {
        override fun createComponent(value: Value<Float>): SettingComponent {
            return PercentSliderComponent(value.getValue())
        }

        override fun createSerializer(value: Value<Float>, data: Any?): Serializer<Float> {
            return TreePropertySerializer(Float::class.java)
        }
    }

    data object Bind : PropertyType<BindData, BindPropertyAttr>() {
        override fun createComponent(value: Value<BindData>, attr: BindPropertyAttr): SettingComponent {
            return BindComponent(value.getValue(), attr.types)
        }

        override fun createSerializer(value: Value<BindData>, data: BindPropertyAttr): Serializer<BindData> {
            return TreePropertySerializer(BindData::class.java)
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

    data object SwitchColor : PropertyType<SwitchColorData, ColorPropertyAttr>() {
        override fun createComponent(value: Value<SwitchColorData>, attr: ColorPropertyAttr): SettingComponent {
            return SwitchColorComponent(value.getValue().enabled, value.getValue().color, attr.alpha)
        }

        override fun createSerializer(value: Value<SwitchColorData>, data: ColorPropertyAttr): PropertySerializer<SwitchColorData> {
            return SwitchColorPropertySerializer(data.alpha)
        }
    }

    data object Selector : PropertyType<Any, SelectorPropertyAttr<*>>() {
        override fun createComponent(value: Value<Any>, attr: SelectorPropertyAttr<*>): SettingComponent {
            @Suppress("UNCHECKED_CAST")
            return SelectorComponent((attr as SelectorPropertyAttr<Any>).indexOf(value.getValue()), attr)
        }

        override fun createSerializer(value: Value<Any>, data: SelectorPropertyAttr<*>): PropertySerializer<Any> {
            return TreePropertySerializer(value.type)
        }
    }

    data object Button : PropertyType<Nothing, ButtonPropertyAttr>() {
        override fun createComponent(value: Value<Nothing>, attr: ButtonPropertyAttr): SettingComponent {
            return ButtonComponent(attr.text, attr.action)
        }

        override fun createSerializer(value: Value<Nothing>, data: ButtonPropertyAttr): PropertySerializer<Nothing> {
            return EmptyPropertySerializer
        }
    }

    data object Widget : PropertyType<WidgetData, WidgetPropertyAttr>() {
        override fun createComponent(value: Value<WidgetData>, attr: WidgetPropertyAttr): SettingComponent {
            return SwitchComponent(value.getValue().enabled).apply {
                observe { value.getValue().enabled = it as Boolean }
            }
        }

        override fun createSerializer(value: Value<WidgetData>, data: WidgetPropertyAttr): PropertySerializer<WidgetData> {
            return WidgetPropertySerializer
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
