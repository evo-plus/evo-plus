package ru.dargen.evoplus.service.user

import ru.dargen.evoplus.service.user.model.UserStatisticModel
import ru.dargen.rest.annotation.Async
import ru.dargen.rest.annotation.RequestHeader
import ru.dargen.rest.annotation.RequestMapping
import ru.dargen.rest.annotation.parameter.Authorization
import ru.dargen.rest.annotation.parameter.Body
import ru.dargen.rest.annotation.parameter.Parameter
import ru.dargen.rest.request.HttpMethod.POST

@RequestMapping("https://evo-plus.dargen.space/api/user")
//@RequestMapping("http://localhost:5353/api")
interface UserController {

    @Async
    @RequestMapping("/update", method = POST)
    fun update(
        @Authorization("Bearer") token: String,
        @Parameter("version") version: String,
        @Parameter("server") server: String,
    )

    @RequestMapping("/statistic/update", method = POST)
    @RequestHeader(key = "Content-Type", value = "application/json")
    fun updateStatistic(
        @Authorization("Bearer") token: String,
        @Parameter("server") server: Int,
        @Body statistic: UserStatisticModel
    )

    @RequestMapping("/active/filter", method = POST)
    @RequestHeader(key = "Accept", value = "application/json")
    @RequestHeader(key = "Content-Type", value = "application/json")
    fun filterActive(@Body names: Collection<String>): Collection<String>

    @RequestMapping("/data/displayNames", method = POST)
    @RequestHeader(key = "Accept", value = "application/json")
    @RequestHeader(key = "Content-Type", value = "application/json")
    fun getDisplayNames(@Body names: Collection<String>): Map<String, String>

}