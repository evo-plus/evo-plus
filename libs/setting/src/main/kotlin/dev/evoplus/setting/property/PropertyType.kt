package dev.evoplus.setting.property

import dev.evoplus.setting.gui.settings.ButtonComponent
import dev.evoplus.setting.gui.settings.CheckboxComponent
import dev.evoplus.setting.gui.settings.ColorComponent
import dev.evoplus.setting.gui.settings.DecimalSliderComponent
import dev.evoplus.setting.gui.settings.NumberComponent
import dev.evoplus.setting.gui.settings.PercentSliderComponent
import dev.evoplus.setting.gui.settings.SelectorComponent
import dev.evoplus.setting.gui.settings.SettingComponent
import dev.evoplus.setting.gui.settings.SliderComponent
import dev.evoplus.setting.gui.settings.SwitchComponent
import dev.evoplus.setting.gui.settings.TextComponent
import dev.evoplus.setting.property.attr.ButtonPropertyAttr
import dev.evoplus.setting.property.attr.ColorPropertyAttr
import dev.evoplus.setting.property.attr.DecimalPropertyAttr
import dev.evoplus.setting.property.attr.NumberPropertyAttr
import dev.evoplus.setting.property.attr.SelectorPropertyAttr
import dev.evoplus.setting.property.attr.TextPropertyAttr

abstract class PropertyType<A>(val type: Class<*>) {

    data object Switch : NoAttr(Boolean::class.java) {
        override fun createComponent(value: PropertyValue): SettingComponent {
            return SwitchComponent(value.getAs())
        }
    }

    data object CheckBox : NoAttr(Boolean::class.java) {
        override fun createComponent(value: PropertyValue): SettingComponent {
            return CheckboxComponent(value.getAs())
        }
    }

    data object Text : PropertyType<TextPropertyAttr>(String::class.java) {
        override fun createComponent(value: PropertyValue, attr: TextPropertyAttr): SettingComponent {
            return TextComponent(value.getAs(), attr.placeholder, false, attr.protected)
        }
    }

    data object Paragraph : PropertyType<TextPropertyAttr>(String::class.java) {
        override fun createComponent(value: PropertyValue, attr: TextPropertyAttr): SettingComponent {
            return TextComponent(value.getAs(), attr.placeholder, true, attr.protected)
        }
    }

    data object PercentSlider : NoAttr(Float::class.java) {
        override fun createComponent(value: PropertyValue): SettingComponent {
            return PercentSliderComponent(value.getAs())
        }
    }

    data object Slider : PropertyType<NumberPropertyAttr>(Int::class.java) {
        override fun createComponent(value: PropertyValue, attr: NumberPropertyAttr): SettingComponent {
            return SliderComponent(value.getAs(), attr.min, attr.max, attr.step)
        }
    }

    data object DecimalSlider : PropertyType<DecimalPropertyAttr>(Float::class.java) {
        override fun createComponent(value: PropertyValue, attr: DecimalPropertyAttr): SettingComponent {
            return DecimalSliderComponent(value.getAs(), attr.min, attr.max, attr.decimals)
        }
    }

    data object Number : PropertyType<NumberPropertyAttr>(Int::class.java) {
        override fun createComponent(value: PropertyValue, attr: NumberPropertyAttr): SettingComponent {
            return NumberComponent(value.getAs(), attr.min, attr.max, attr.step)
        }
    }

    data object Color : PropertyType<ColorPropertyAttr>(Color::class.java) {
        override fun createComponent(value: PropertyValue, attr: ColorPropertyAttr): SettingComponent {
            return ColorComponent(value.getAs(), attr.alpha)
        }
    }

    data object Selector : PropertyType<SelectorPropertyAttr<*>>(Int::class.java) {
        override fun createComponent(value: PropertyValue, attr: SelectorPropertyAttr<*>): SettingComponent {
            @Suppress("UNCHECKED_CAST")
            return SelectorComponent((attr as SelectorPropertyAttr<Any>).indexOf(value.getAs()), attr)
        }
    }

    data object Button : PropertyType<ButtonPropertyAttr>(Nothing::class.java) {
        override fun createComponent(value: PropertyValue, attr: ButtonPropertyAttr): SettingComponent {
            return ButtonComponent(attr.text)
        }
    }

    val name get() = toString()

    abstract fun createComponent(value: PropertyValue, data: A): SettingComponent

    abstract class NoAttr(type: Class<*>) : PropertyType<Any?>(type) {

        override fun createComponent(value: PropertyValue, data: Any?) = createComponent(value)
        abstract fun createComponent(value: PropertyValue): SettingComponent

    }

}
