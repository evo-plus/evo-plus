package ru.dargen.evoplus.protocol.collector.data

import ru.dargen.evoplus.protocol.registry.PetType

data class PetData(private val pet: String, val level: Int, val exp: Double, val energy: Double) {

    val type get() = PetType.valueOf(pet)!!

    companion object {

        fun random() = if (PetType.values.isEmpty()) null else PetType.values.random().let { PetData(it.id, 5, 512.0, 5.0) }

    }

}