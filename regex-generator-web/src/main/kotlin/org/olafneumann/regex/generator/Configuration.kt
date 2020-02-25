package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.regex.Recognizer

data class Configuration(
    val recognizers: List<Recognizer>,
    val mainGroupName: String? = null,
    val mainGroupIndex: Int? = 1
) {
    companion object {
        // val config = js("require('settings.json')") as Configuration
        val default = Configuration(recognizers = listOf(
            Recognizer("number", "[0-9]+"),
            Recognizer("date", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
            Recognizer("real","[0-9]*\\.[0-9]+"),
            Recognizer("day","(0?[1-9]|[12][0-9]|3[01])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
            Recognizer("month","(0?[1-9]|[1][0-2])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
            Recognizer("time","[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{1,3})?"),
            Recognizer("ISO8601","[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?"),
            Recognizer("String 1","'([^']|\\\\')*'"),
            Recognizer("String 2","\"([^\"]|\\\\')*\""),
            Recognizer("Hashtag","\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"),
            Recognizer("loglevel","(TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|SEVERE|FATAL)"),
            Recognizer("Characters","[a-zA-Z]+")
        ))

        fun fromCopy(configuration: dynamic) : Configuration {
            return Configuration(
                recognizers = (configuration.recognizers as Array<Recognizer>).map {
                    Recognizer(
                        name = it.name,
                        outputPattern = it.outputPattern,
                        searchPattern = it.searchPattern,
                        description = it.description,
                        active = it.active
                    )
                },
                mainGroupIndex = configuration.mainGroupIndex as Int?,
                mainGroupName = configuration.mainGroupName as String?
            )
        }
    }
}