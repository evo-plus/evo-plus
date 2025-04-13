package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.evoplus.feature.setting.property.value.WidgetData
import gg.essential.elementa.utils.Vector3f

@OptIn(ExperimentalStdlibApi::class)
data object WidgetPropertySerializer : PropertySerializer<WidgetData> {

    override fun serialize(value: WidgetData): Result<JsonElement> {
        return Result.success(JsonObject().apply {
            addProperty("enabled", value.enabled)
            add("align", JsonObject().apply {
                addProperty("x", value.align.x)
                addProperty("y", value.align.y)
                addProperty("z", value.align.z)
            })
            add("origin", JsonObject().apply {
                addProperty("x", value.origin.x)
                addProperty("y", value.origin.y)
                addProperty("z", value.origin.z)
            })
            addProperty("scale", value.scale)
        })
    }

    override fun deserialize(json: JsonElement) = runCatching {
        json as JsonObject
        WidgetData(
            json["enabled"]?.asBoolean != false,
            json["align"].asJsonObject
                ?.let { Vector3f(it["x"].asFloat, it["y"].asFloat, it["z"].asFloat) } ?: Vector3f(0f, 0f, 0f),
            json["origin"].asJsonObject
                ?.let { Vector3f(it["x"].asFloat, it["y"].asFloat, it["z"].asFloat) } ?: Vector3f(0f, 0f, 0f),
            json["scale"].asDouble
        )
    }

}