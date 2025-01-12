package ru.dargen.evoplus.service.visual

import net.minecraft.text.Text
import net.minecraft.text.Texts
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.api.event.player.PlayerDisplayNameEvent
import ru.dargen.evoplus.resource.builtin.Symbol
import ru.dargen.evoplus.service.user.UserService

object PlayerNames {

    init {
        on<PlayerDisplayNameEvent> {
            if (UserService.isActiveUser(playerName)) {
                displayName = displayName.asUserText()
            }
        }
    }

    private fun Text.asUserText() = Texts.join(listOf(Symbol.EP, this), Text.of(" "))

}