package ru.dargen.evoplus.util.rest

import ru.dargen.rest.RestClientFactory

val RestClient = RestClientFactory.createHttpBuiltinClient()

inline fun <reified C> controller() = RestClient.createController(C::class.java)