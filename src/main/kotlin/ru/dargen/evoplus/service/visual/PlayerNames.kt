package ru.dargen.evoplus.service.visual

import ru.dargen.evoplus.service.user.UserService

object PlayerNames {

    fun apply(name: String) = UserService.getDisplayName(name) ?: name

}