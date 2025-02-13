package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.evoplus.feature.setting.property.value.SwitchColor
import java.awt.Color

@OptIn(ExperimentalStdlibApi::class)
class SwitchColorPropertySerializer(val alpha: Boolean) : PropertySerializer<SwitchColor> {

    override fun serialize(value: SwitchColor) = runCatching {
        JsonObject().apply {
            addProperty("enabled", value.enabled)
            addProperty("color", value.color.rgb.toHexString(HexFormat.UpperCase))
        }
    }

    override fun deserialize(json: JsonElement) = runCatching {
        val data = json.asJsonObject

        SwitchColor(
            data["enabled"].asBoolean,
            Color(data["color"].asJsonPrimitive.asString.hexToInt(HexFormat.UpperCase), alpha)
        )
    }

}