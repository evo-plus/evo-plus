package ru.dargen.evoplus.features.share

import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeatureElementProvider
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.BooleanSetting
import ru.dargen.evoplus.render.Colors
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.animation.animate
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.input.button
import ru.dargen.evoplus.render.node.input.input
import ru.dargen.evoplus.render.node.rectangle
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.async
import ru.dargen.evoplus.util.PasteApi
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.sendClanMessage
import ru.dargen.evoplus.util.minecraft.sendCommand

class ShareSetting(
    id: String, name: String,
    val encoder: (nick: String?) -> String,
    val decoder: (nick: String, data: String) -> Unit,
) : BooleanSetting(id, name, true), FeatureElementProvider {

    override val element = object : FeatureElement {
        override fun createElement(prompt: FeaturePrompt) = rectangle {
            color = Colors.TransparentBlack
            size = v3(y = 55.0)

            +text(prompt.highlightPrompt(name)) {
                translation = v3(6.6, 15.0)
                origin = Relative.LeftCenter
            }

            +hbox {
                align = Relative.LeftBottom
                origin = Relative.LeftCenter
                translation = v3(y = -20.0)

                val input = +input {
                    this.prompt.text = "Введите ник"
                    maxLength = 16
                    strictSymbols()
                    filter { "[a-zA-Z0-9_]".toRegex().matches(it.toString()) }
                }
                +button("В клан") {
                    on { async { share(null) } }
                }
                +button("Игроку") {
                    on {
                        if (input.length !in 3..16) {
                            input.animate("warn", .2) {
                                input.color = Colors.Negative
                                next("warn", .05) {
                                    input.color = Colors.Second
                                }
                            }
                        } else {
                            val nick = input.content

                            async {
                                share(nick)
                                input.clear()
                            }
                            input.animate("warn", .2) {
                                input.color = Colors.Positive
                                next("warn", .05) {
                                    input.color = Colors.Second
                                }
                            }

                        }
                    }
                }
            }

            +button(value.stringfy()) {
                align = Relative.RightTop
                origin = Relative.RightCenter
                translation = v3(-5.0, 25.0)

                on {
                    value = !value
                    label.text = value.stringfy()
                }
            }
        }

        override fun search(prompt: FeaturePrompt): Boolean {
            return prompt.shouldPass(name)
        }
    }

    fun share(nick: String?) = generate(nick).also { content ->
        nick?.let { sendCommand("m $it $content") } ?: sendClanMessage(content)
    }

    fun generate(nick: String?) = "evoplus:$id:${PasteApi.paste(encoder(nick))}"

}