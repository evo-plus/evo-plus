package ru.dargen.evoplus.util.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

val GsonBuilder = GsonBuilder().setPrettyPrinting()
var PrettyGson = GsonBuilder.create()
var Gson = GsonBuilder().create()
fun gson(bake: Boolean = true, block: GsonBuilder.() -> Unit): Gson {
    GsonBuilder.block()
    if (bake) PrettyGson = GsonBuilder.create()
    return PrettyGson
}

inline fun <reified T : Any> GsonBuilder.deserializer(
    type: Type = type<T>(),
    noinline deserializer: (element: JsonElement, ctx: JsonDeserializationContext) -> T
) = registerTypeAdapter(type, DelegateJsonDeserializer(deserializer))

inline fun <reified T : Any> GsonBuilder.serializer(
    type: Type = type<T>(),
    noinline serializer: (any: T, ctx: JsonSerializationContext) -> JsonElement
) = registerTypeAdapter(type, DelegateJsonSerializer(serializer))

inline fun <reified T : Any> GsonBuilder.adapter(
    noinline serializer: (obj: T, ctx: JsonSerializationContext) -> JsonElement,
    noinline deserializer: (element: JsonElement, ctx: JsonDeserializationContext) -> T,
    type: Type = type<T>()
) = deserializer(type, deserializer).serializer(type, serializer)


inline fun <reified T : Any> type() = object : TypeToken<T>() {}.type

inline fun <reified T : Any> fromJson(json: String, type: Type = type<T>()) = PrettyGson.fromJson<T>(json, type)

inline fun <reified T : Any> fromJson(json: JsonElement, type: Type = type<T>()) = PrettyGson.fromJson<T>(json, type)

fun toJson(any: Any) = PrettyGson.toJson(any)

fun toJsonTree(any: Any) = PrettyGson.toJsonTree(any)