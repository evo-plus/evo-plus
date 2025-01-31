package ru.dargen.evoplus.util.selector

import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class selector<E : Enum<E>>(val enumClass: Class<E>, index: Int = 0) :
    ListSelector<E>(enumClass.enumConstants.toList(), index)

inline fun <reified E : Enum<E>> selector(index: Int = 0) =
    selector(E::class.java, if (index == -1) E::class.java.enumConstants.size - 1 else index)