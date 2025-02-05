package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import dev.evoplus.feature.setting.PropertyGson
import java.lang.reflect.Type


class TreePropertySerializer<V>(val type: Type) : PropertySerializer<V> {

    override fun serialize(value: V) = value.runCatching(PropertyGson::toJsonTree)

    override fun deserialize(json: JsonElement) = json.runCatching { PropertyGson.fromJson<V>(json, type) }

}