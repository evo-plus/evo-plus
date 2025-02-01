package dev.evoplus.feature.setting.example

import dev.evoplus.feature.setting.Settings
import gg.essential.universal.UChat
import java.awt.Color
import kotlin.io.path.Path


object TestSettings : Settings(Path("run/evo-plus/test.json")) {

    var checkbox = false
    var switch = false
    var text = ""
    var paragraph = ""
    var percent = 0f
    var slider = 0
    var decimal = 0f
    var number = 0
    var color: Color = Color.WHITE
    var selector = TestSelector.SECOND

    init {
        category("overview", "Property Overview") {
            checkbox(::checkbox, "Checkbox")
            switch(::switch, "Switch")
            text(::text, "Text", "Single line text", placeholder = "Text")
            paragraph(::paragraph, "Paragraph", placeholder = "Paragraph")
            percent(::percent, "Percent", "Percent slider from 0 to 1 (0 to 100)")
            slider(::slider, "Slider","Slider with range and step", range = 0..10 step 2)
            decimal(::decimal, "Decimal", "With range and decimals format", range = 0f..12f, decimals = 2)
            number(::number, "Number", "Number with range and step", range = 0..10 step 2)
            color(::color, "Color", "With alpha", alpha = true)
            selector(::selector, "Selector", "With toString", toString = TestSelector::display)
            button("Button", "Button with action") { UChat.chat("demoButton clicked!") }
        }

        initialize()
    }

}
