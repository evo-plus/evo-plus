package ru.dargen.evoplus.service.visual

import net.minecraft.text.Text
import net.minecraft.text.Texts
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.player.AccessPlayerNameEvent
import ru.dargen.evoplus.event.player.PlayerDisplayNameEvent
import ru.dargen.evoplus.resource.builtin.Symbol
import ru.dargen.evoplus.service.user.UserService
import ru.dargen.evoplus.util.text.TextSpace

object PlayerVisual {

    //language=regexp
    val ChatPatterns = listOf(
        "^[ⓂⓁⒼ].*?([a-zA-Z0-9_]{2,16}).*\$" to true,
        "^Игроку ([a-zA-Z0-9_]{2,16}).*$" to false,
        "^\\[Клан].*?([a-zA-Z0-9_]{2,16}).*$" to true,
        "^ЛС \\| .*?([a-zA-Z0-9_]{2,16}).*$" to false
    ).toMap().mapKeys { it.key.toRegex() }

    init {
//        on<ChatReceiveEvent> {
//            val (player, prefix) = ChatPatterns.firstNotNullOfOrNull {
//                it.key.find(message.string)?.groupValues?.get(1)?.to(it.value)
//            } ?: return@on
//            message = PlayerTextContent.replace(message, player, PlayerContentOptions(prefix))
//        }
        on<AccessPlayerNameEvent> { name = PlayerNames.apply(name) }
        on<PlayerDisplayNameEvent> { if (UserService.isActiveUser(playerName)) displayName = displayName.asUserText() }
    }

    private fun Text.asUserText() = Texts.join(listOf(Symbol.EP, this), TextSpace)

}