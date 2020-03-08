package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.regex.BracketedRecognizer.CenterPattern

object RecognizerRegistry {
    private val recognizers = listOf<Recognizer>(
        EchoRecognizer("Character", ".", priority = 0),
        SimpleRecognizer("One character", "[a-zA-Z]"),
        SimpleRecognizer("Multiple characters", "[a-zA-Z]+"),
        SimpleRecognizer("Digit", "\\d"),
        SimpleRecognizer("Number", "[0-9]+"),
        SimpleRecognizer("Decimal number", "[0-9]*\\.[0-9]+"),
        SimpleRecognizer("Day", "(0?[1-9]|[12][0-9]|3[01])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        SimpleRecognizer("Month", "(0?[1-9]|[1][0-2])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        // TODO hour, minute/ second, degree
        SimpleRecognizer("Date", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
        SimpleRecognizer("Time", "[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{1,3})?"),
        SimpleRecognizer(
            "ISO8601",
            "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?"
        ),
        SimpleRecognizer("Hashtag", "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"),
        SimpleRecognizer("Log level", "(TRACE|DEBUG|INFO|NOTICE|WARN|ERROR|SEVERE|FATAL)"),
        BracketedRecognizer(
            "Round brackets", "\\(",
            listOf(
                CenterPattern("no round bracket", "[^)]*")
                //,CenterPattern("escaped round bracket", "(?:[^)]|\\\\))*")
            ),
            "\\)", "(\\()([^)]*)(\\))"
        ),
        BracketedRecognizer(
            "Square brackets", "\\[",
            listOf(
                CenterPattern("no square bracket", "[^\\]]*")
                //,CenterPattern("escaped square bracket", "(?:[^\\]]|\\\\\\])*")
            ), "]", "(\\[)([^\\]]*)(])"
        ),
        BracketedRecognizer(
            "Curly braces", "\\{",
            listOf(
                CenterPattern("no curly braces", "[^}]*")
                //,CenterPattern("escaped curly braces", "(?:[^}]|\\\\})*")
            ), "}", "(\\{)([^}]*)(})"
        ),
        BracketedRecognizer(
            "String (quotation mark)", "\"",
            listOf(
                CenterPattern("no quotation mark", "[^\"]*"),
                CenterPattern("escaped quotation mark", "(?:[^\"]|\\\\')*")
            ),
            "\"", "(\")([^\"]*)(\")"
        ),
        BracketedRecognizer(
            "String (apostrophe)", "'",
            listOf(
                CenterPattern("no apostrophe", "[^']*"),
                CenterPattern("escaped apostrophe", "(?:[^']|\\\\')*")
            ),
            "'", "(')([^']*)(')"
        )
    )

    fun findMatches(input: String): List<RecognizerMatch> {
        val matches = recognizers
            .filter { it.active }
            .flatMap { it.findMatches(input) }
            .sortedWith(HasRange.byPosition)
            .toMutableList()

        matches.addAll(findRepetitions(matches))

        return matches
    }

    private fun findRepetitions(matches: List<RecognizerMatch>): List<RecognizerMatch> {
        return emptyList()
    }

    private fun combineClassesInBracketedRecognizers(): List<RecognizerMatch> {
        return emptyList()
    }
}
