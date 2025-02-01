package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import kotlin.Nothing

object EmptyPropertySerializer : PropertySerializer<Nothing> {

    override fun serialize(value: Nothing) =
        Result.failure<Nothing>(IllegalStateException("Empty property cannot serialize"))

    override fun deserialize(json: JsonElement) =
        Result.failure<Nothing>(IllegalStateException("Empty property cannot deserialize"))

}