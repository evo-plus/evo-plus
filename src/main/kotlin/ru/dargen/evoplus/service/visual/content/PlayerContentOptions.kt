package ru.dargen.evoplus.service.visual.content

data class PlayerContentOptions(val prefix: Boolean = true, val placeholder: Boolean = true) {

    companion object {

        val Prefix = PlayerContentOptions(placeholder = false)
        val Placeholder = PlayerContentOptions(prefix = false)
        val All = PlayerContentOptions()

    }

}