package ru.dargen.evoplus.features.clicker

import ru.dargen.evoplus.mixin.MinecraftClientAccessor
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.ClientAccessor
import ru.dargen.evoplus.util.minecraft.ClientExtension

enum class ClickerMouse(val display: String) {
    
    LEFT("ЛКМ") {
        override fun invoke() {
            val attackCooldown = ClientAccessor.attackCooldown

            if (attackCooldown == 10000) ClientAccessor.attackCooldown = 0

            Client.options.attackKey.isPressed = true
            ClientAccessor.leftClick()
            Client.options.attackKey.isPressed = false
        }
    },
    RIGHT("ПКМ") {
        override fun invoke() {
            ClientExtension.`evo_plus$rightClick`()
        }
    };

    abstract operator fun invoke()

    override fun toString() = display
}