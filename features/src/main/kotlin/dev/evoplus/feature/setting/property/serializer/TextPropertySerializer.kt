package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

class TextPropertySerializer(val protected: Boolean) : PropertySerializer<String> {

    override fun serialize(value: String) = runCatching { JsonPrimitive(value) }

    override fun deserialize(json: JsonElement) = runCatching { json.asJsonPrimitive.asString }

}