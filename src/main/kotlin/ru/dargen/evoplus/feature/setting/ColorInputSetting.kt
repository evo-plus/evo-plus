package ru.dargen.evoplus.feature.setting

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeatureElementProvider
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.box.vbox
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.render.node.input.input
import ru.dargen.evoplus.render.node.rectangle
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.util.math.v3
import java.awt.Color

class ColorInputSetting(
    id: String, name: String, value: Boolean
) : Setting<Boolean>(id, name), FeatureElementProvider {

    override var value: Boolean = value
        set(value) {
            field = value
            handler(value)
        }

    final var mirroring = false

    val inputs = buildList {
        repeat(2) {
            add(input {
                maxLength = 6
                strictSymbols()
                filter { "[a-fA-F0-9]".toRegex().matches(it.toString()) }
                on { it.toIntOrNull(16)?.run { text.color = Color(this) } }
            })
        }
    }

    override fun load(element: JsonElement) {
        element.asJsonObject.get(id)?.asJsonObject?.apply {
            value = get("colorize").asBoolean
            mirroring = get("mirroring").asBoolean
            inputs.forEachIndexed { index, inputNode ->
                inputNode.content = get("chatcolor_$index").asString.apply {
                    toIntOrNull(16)?.run { inputNode.text.color = Color(this) }
                }
            }
        }
    }

    override fun store() = JsonObject().apply {
        addProperty("colorize", value)
        addProperty("mirroring", mirroring)
        inputs.forEachIndexed { index, inputNode ->
            addProperty("chatcolor_$index", inputNode.content)
        }
    }

    private val mirrorButton = run {
        fun Boolean.stringify() = if (this) "Зеркальность" else "Градация"

        button(mirroring.stringify()) {
            on {
                mirroring = !mirroring
                label.text = mirroring.stringify()
            }
        }
    }

    private val resetButton = button("Сбросить") {
        on { inputs.forEach { it.content = "ffffff" } }
    }

    override val element = object : FeatureElement {
        override fun createElement(prompt: FeaturePrompt) = rectangle {
            //TODO: исправить отображение кнопки (потому что слава дебил и оно теперь нормально не подгружает)
            fun Boolean.stringfy() = if (this) "§aВключено" else "§cВыключено"

            color = Colors.TransparentBlack
            size = v3(y = 60.0)

            +text(prompt.highlightPrompt(name)) {
                translation = v3(6.6, 15.0)
                origin = Relative.LeftCenter
            }

            +hbox {
                align = Relative.LeftBottom
                origin = Relative.LeftCenter
                translation = v3(y = -20.0)
                inputs.forEach { +it }
                +resetButton
            }

            +vbox {
                align = Relative.RightBottom
                origin = Relative.RightCenter
                translation = v3(y = -30.0)

                +button(value.stringfy()) {
                    on {
                        this@ColorInputSetting.value = !this@ColorInputSetting.value
                        label.text = this@ColorInputSetting.value.stringfy()
                    }
                }
                +mirrorButton
            }
        }

        override fun search(prompt: FeaturePrompt): Boolean {
            return prompt.shouldPass(name)
        }
    }
}