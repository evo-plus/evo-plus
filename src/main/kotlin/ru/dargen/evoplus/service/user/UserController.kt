package ru.dargen.evoplus.service.user

import ru.dargen.rest.annotation.RequestHeader
import ru.dargen.rest.annotation.RequestMapping
import ru.dargen.rest.annotation.parameter.Authorization
import ru.dargen.rest.annotation.parameter.Body
import ru.dargen.rest.annotation.parameter.Parameter
import ru.dargen.rest.request.HttpMethod.POST

@RequestMapping("https://evo-plus.dargen.space/api")
//@RequestMapping("http://localhost:5353/api")
interface UserController {

    @RequestMapping("/user/update", method = POST)
    @RequestHeader(key = "Accept", value = "application/json")
    fun update(
        @Authorization("Bearer") token: String,
        @Parameter("version") version: String, @Parameter("server") server: String,
    )

    @RequestMapping("/user/active/filter", method = POST)
    @RequestHeader(key = "Accept", value = "application/json")
    @RequestHeader(key = "Content-Type", value = "application/json")
    fun filterActive(@Body names: Collection<String>): Collection<String>

}