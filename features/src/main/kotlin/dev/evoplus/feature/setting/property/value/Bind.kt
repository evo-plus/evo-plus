package dev.evoplus.feature.setting.property.value

import org.lwjgl.glfw.GLFW

data class Bind(var type: Type, var code: Int) {

    val isBound get() = type !== Type.NONE
    val buttonName get() = type.getName(code)

    companion object {

        fun none() = Bind(Type.NONE, -1)

        fun mouse(code: Int) = Bind(Type.MOUSE, code)

        fun key(code: Int) = Bind(Type.KEYBOARD, code)

    }

    enum class Type(val display: String, val getName: (Int) -> String) {

        NONE("", { "Нет" }),
        MOUSE("Мышь", {
            when (it) {
                0 -> "ЛКМ"
                1 -> "ПКМ"
                2 -> "СКМ"
                else -> "Кнопка $it"
            }
        }),
        KEYBOARD("Клавиатура", { GLFW.glfwGetKeyName(it, -1)?.uppercase()?.let("Клавиша "::plus) ?: "Клавиша $it" })

    }

}