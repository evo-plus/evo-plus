package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement

interface PropertySerializer<V> {

    fun serialize(value: V): Result<JsonElement>

    fun deserialize(json: JsonElement): Result<V>

}