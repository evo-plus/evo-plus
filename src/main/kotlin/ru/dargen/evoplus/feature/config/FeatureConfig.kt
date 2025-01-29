package ru.dargen.evoplus.feature.config

import com.google.gson.reflect.TypeToken
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.util.catch
import ru.dargen.evoplus.util.json.Gson
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writeText
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class FeatureConfig<T>(val name: String, val token: TypeToken<T>, var value: T) : ReadWriteProperty<Any, T> {

    val file = EvoPlus.Folder.resolve("$name.json")

    fun load() {
        catch("Error while loading config: $name.json") {
            if (file.exists()) {
                Gson.fromJson(file.reader(), token)?.let(this::value::set)
            }
        }
    }

    fun save() {
        catch("Error while saving config: $name.json") { file.writeText(Gson.toJson(value)) }
    }

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }

}