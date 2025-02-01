package dev.evoplus.feature.setting.property.serializer

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.awt.Color

@OptIn(ExperimentalStdlibApi::class)
class ColorPropertySerializer(val alpha: Boolean) : PropertySerializer<Color> {

    override fun serialize(value: Color) = runCatching {
        JsonPrimitive(value.rgb.toHexString(HexFormat.UpperCase))
    }

    override fun deserialize(json: JsonElement) = runCatching {
        Color(json.asJsonPrimitive.asString.hexToInt(HexFormat.UpperCase), alpha)
    }

}