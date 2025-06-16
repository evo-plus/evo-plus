package ru.dargen.evoplus.protocol.collector

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import pro.diamondworld.protocol.util.ProtocolSerializable
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.util.json.Gson
import ru.dargen.evoplus.util.json.type
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.kotlin.cast
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class DataCollector<P : ProtocolSerializable>(val packetClass: KClass<P>, val extractor: (P) -> Map<String, String>){

    val collectors = mutableMapOf<String, DataCollectorEntry<Any>>()

    init {
        listen(packetClass) { accept(extractor(it)) }
    }

    private fun accept(values: Map<String, String>) {
        collectors.forEach { (name, data) -> values[name]?.let(data::accept) }
    }

    inline fun <reified T : Any> collect(
        name: String,
        default: T? = null,
        type: Type = type<T>(),
        noinline block: (T) -> Unit = {}
    ) = DataCollectorEntry(default, type, block).also { collectors[name] = it.cast() }

    @KotlinOpens
    class DataCollectorEntry<T>(default: T?, val type: Type, val consumer: (T) -> Unit) {

        var value = default

        fun accept(value: String) {
            val jsonElement = JsonParser.parseString(value)

            val deserialized: T = when {
                jsonElement.isJsonArray -> Gson.fromJson(jsonElement, type)
                jsonElement.isJsonObject && type.toString().startsWith("java.util.List") -> {
                    val wrapped = JsonArray().also { it.add(jsonElement) }
                    Gson.fromJson(wrapped, type)
                }
                else -> Gson.fromJson(jsonElement, type)
            }

            consumer(deserialized)
            this@DataCollectorEntry.value = deserialized
        }

        operator fun getValue(thisRef: Any, property: KProperty<*>) = value!!

        operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            this.value = value
        }

    }

}
