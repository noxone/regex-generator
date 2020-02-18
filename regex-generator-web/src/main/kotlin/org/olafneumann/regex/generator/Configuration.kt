package org.olafneumann.regex.generator

data class Configuration(
    val recognizers: List<Recognizer>,
    val mainGroupName: String? = null,
    val mainGroupIndex: Int? = 1
) {
    companion object {
        // val config = js("require('settings.json')") as Configuration
        val default = Configuration(recognizers = listOf(
                Recognizer("number", "[0-9]+"),
            Recognizer("date", "[0-9]{4}-[0-9]{2}-[0-9]{2}")
        ))
    }
}