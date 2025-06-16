package ru.dargen.evoplus.protocol.collector.data

import ru.dargen.evoplus.protocol.registry.PetHolder
import ru.dargen.evoplus.protocol.registry.PetType

data class PetData(val pet: PetHolder, val level: Int, val exp: Double, val energy: Double) {

    val type get() = pet.get()!!

    companion object {

        fun random() =
            if (PetType.values.isEmpty()) null else PetType.values.random().let { PetData(it.holder, 5, 512.0, 5.0) }

    }

}