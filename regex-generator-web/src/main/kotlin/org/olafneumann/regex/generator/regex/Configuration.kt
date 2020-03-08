package org.olafneumann.regex.generator.regex

data class Configuration(
    val recognizers: List<Recognizer>
) {
    companion object {
        // val config = js("require('settings.json')") as Configuration
        val default = Configuration(
            recognizers = listOf<Recognizer>(
                SimpleRecognizer("number", "[0-9]+"),
                SimpleRecognizer("date", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
                SimpleRecognizer("real", "[0-9]*\\.[0-9]+"),
                SimpleRecognizer("day", "(0?[1-9]|[12][0-9]|3[01])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
                SimpleRecognizer("month", "(0?[1-9]|[1][0-2])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
                SimpleRecognizer("time", "[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{1,3})?"),
                SimpleRecognizer(
                    "ISO8601",
                    "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?"
                ),
                SimpleRecognizer("String 1", "'([^']|\\\\')*'"),
                SimpleRecognizer("String 2", "\"([^\"]|\\\\')*\""),
                SimpleRecognizer("Hashtag", "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"),
                SimpleRecognizer("Log level", "(TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|SEVERE|FATAL)"),
                SimpleRecognizer("Characters", "[a-zA-Z]+"),
                BracketedRecognizer("Square Brackets", "\\[", "[^\\]]*", "]", "(\\[)([^\\]]*)(])"),
                BracketedRecognizer("String (quotation mark)", "\"", "[^\"]*", "\"", "(\")([^\"]*)(\")"),
                BracketedRecognizer("String (apostrophe)", "'", "[^']*", "'", "(')([^']*)(')")
            )
        )
    }
}