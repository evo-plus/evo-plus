package ru.dargen.evoplus.feature.screen

data class FeaturePrompt(val prompt: String? = null) {


    fun shouldPass(text: String) = prompt == null || text.contains(prompt, ignoreCase = true)

    fun highlightPrompt(text: String) = prompt?.let { text.replace(it, "§e$it§r", ignoreCase = true) } ?: text

}