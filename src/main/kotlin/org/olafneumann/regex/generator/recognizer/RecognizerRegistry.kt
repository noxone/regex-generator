@file:Suppress("MaxLineLength")

package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.utils.HasRange
import org.olafneumann.regex.generator.recognizer.BracketedRecognizer.CenterPattern

object RecognizerRegistry {
    private val recognizers = listOf(
        EchoRecognizer("Character", ".", priority = 10),
        EchoRecognizer("Exact number", "[0-9]{2,}"),
        EchoRecognizer("Repeating character", "(.)\\1{2,}", outputRegexBuilder = "%1\$s+"),
        SimpleRecognizer("One whitespace", "\\s"),
        SimpleRecognizer("Whitespaces", "\\s+"),
        SimpleRecognizer("One character", "[a-zA-Z]"),
        SimpleRecognizer("Multiple characters", "[a-zA-Z]+"),
        SimpleRecognizer("Digit", "\\d"),
        SimpleRecognizer("Number", "[0-9]+"),
        SimpleRecognizer("Decimal number", "[0-9]*\\.[0-9]+"),
        SimpleRecognizer(
            "Floating point number (with optional exponent)",
            "([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[eE]([+-]?\\d+))?",
            searchPattern = "(%s)\\b"
        ),
        SimpleRecognizer("Alphanumeric characters", "[A-Za-z0-9]+"),
        SimpleRecognizer("URL encoded character", "%[0-9A-Fa-f][0-9A-Fa-f]"),
        SimpleRecognizer("Day", "(0?[1-9]|[12][0-9]|3[01])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        SimpleRecognizer("Month", "(0?[1-9]|[1][0-2])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        SimpleRecognizer("Hour", "(0?[0-9]|1[0-9]|2[0-3])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        SimpleRecognizer("Minute/ Second", "(0?[0-9]|[1-5][0-9])", searchPattern = "(?:^|\\D)(%s)($|\\D)"),
        SimpleRecognizer("Date", "[0-9]{4}-[0-9]{2}-[0-9]{2}"),
        SimpleRecognizer("Time", "[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]{1,3})?"),
        SimpleRecognizer(
            "ISO8601",
            "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?"
        ),
        SimpleRecognizer(
            "RFC2822 e-mail",
            "[-a-z0-9!#\$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#\$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9](?:[-a-z0-9]*[a-z0-9])?\\.)+[a-z0-9](?:[-a-z0-9]*[a-z0-9])?"
        ),
        SimpleRecognizer(
            "IPv4 address",
            "\\b(?:(?:2(?:[0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\.){3}(?:(?:2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9]))\\b"
        ),
        SimpleRecognizer(
            "UUID",
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
            searchPattern = "\\b(%s)\\b"
        ),
        SimpleRecognizer("Hashtag", "\\B#([a-z0-9]{2,})(?![~!@#$%^&*()=+_`\\-\\|\\/'\\[\\]\\{\\}]|[?.,]*\\w)"),
        SimpleRecognizer("Simple CSS Color", "#(?:[a-f0-9]{3}){1,2}\\b"),
        SimpleRecognizer("Log level", "(TRACE|DEBUG|INFO|NOTICE|WARN|WARNING|ERROR|SEVERE|FATAL)"),
        BracketedRecognizer(
            name = "Parentheses",
            startPattern = "\\(",
            endPattern = "\\)",
            centerPatterns = listOf(
                CenterPattern("no parentheses", "[^)]*")
            ),
        ),
        BracketedRecognizer(
            name = "Square brackets",
            startPattern = "\\[",
            endPattern = "\\]",
            centerPatterns = listOf(
                CenterPattern("no square bracket", "[^\\]]*")
            )
        ),
        BracketedRecognizer(
            name = "Curly braces",
            startPattern = "\\{",
            endPattern = "\\}",
            centerPatterns = listOf(
                CenterPattern("no curly braces", "[^}]*")
            )
        ),
        BracketedRecognizer(
            name = "String (quotation mark)",
            startPattern = "\"",
            endPattern = "\"",
            centerPatterns = listOf(
                CenterPattern("no quotation mark", "[^\"]*"),
                CenterPattern("backslash escaped quotation mark", """(?:[^\\"]|\\\\|\\")*"""),
                CenterPattern("double escaped quotation mark", """(?:[^"]|"")*""")
            )
        ),
        BracketedRecognizer(
            name = "String (apostrophe)",
            startPattern = "'",
            endPattern = "'",
            centerPatterns = listOf(
                CenterPattern("no apostrophe", "[^']*"),
                CenterPattern("backslash escaped apostrophe", """(?:[^\\']|\\\\|\\')*"""),
                CenterPattern("double escaped apostrophe", """(?:[^']|'')*""")
            )
        )
    )

    val allRecognizerNames: List<String> by lazy { recognizers.map { it.name } }

    fun findMatches(input: String): List<RecognizerMatch> {
        val matches = recognizers
            .flatMap { it.findMatches(input) }
            .sortedWith(HasRange.byPosition)
            .toMutableList()

        matches.addAll(findRepetitions(matches).flatMap { it.findMatches(input) })

        return matches
    }

    private fun findRepetitions(allMatches: List<RecognizerMatch>): Collection<Recognizer> {
        // In this case we will search for matches that appear at least three times in a row
        // with a common distance and the same "separators"

        return allMatches.groupBy { it.recognizer }
            .values
            .flatMap { findRepetitionsPerGroup(allMatches, it) }
            .toSet()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun findRepetitionsPerGroup(
        allMatches: List<RecognizerMatch>,
        groupedMatches: List<RecognizerMatch>
    ): Collection<Recognizer> {
        val possibleRepetitions = groupedMatches.combine { left, right ->
            Distance.between(left, right).matchesInRange(allMatches)
        }.combine { left, right ->
            left.element.findSameMatchesWith((right.element))
        }

        return possibleRepetitions
            .filter { it.element.isNotEmpty() }
            .mapNotNull { combination ->
                try {
                    val startMatch = combination.leftParent.leftParent
                    val mainMatches = listOf(combination.element[0], combination.leftParent.rightParent)
                    SimpleRecognizer(
                        name = "Combination [${startMatch.recognizer.name} + ${mainMatches[0].recognizer.name}]",
                        outputPrePattern = "(${startMatch.patterns[0]}(${mainMatches[0].patterns[0]}${mainMatches[1].patterns[0]})+)",
                        isDerived = true
                    )
                } catch (throwable: Throwable) {
                    console.warn(throwable)
                    null
                }
            }
            .toSet()
    }

    private fun List<RecognizerMatch>.findSameMatchesWith(other: List<RecognizerMatch>): List<RecognizerMatch> {
        if (isEmpty() || other.isEmpty()) {
            return emptyList()
        }
        fun Collection<RecognizerMatch>.filterForRanges(count: Int) = filter { it.ranges.size == count }
        fun Collection<RecognizerMatch>.mostLeft() = flatMap { it.ranges }.minOfOrNull { it.first } ?: Int.MIN_VALUE
        fun Collection<RecognizerMatch>.mostRight() = flatMap { it.ranges }.maxOfOrNull { it.last } ?: Int.MAX_VALUE
        fun Collection<RecognizerMatch>.findFullMatch(first: Int, last: Int) =
            filter { it.ranges[0].first == first && it.ranges[0].last == last }

        val leftMatches = filterForRanges(1)
        val rightMatches = other.filterForRanges(1)
        val leftMin = leftMatches.mostLeft()
        val leftMax = leftMatches.mostRight()
        val rightMin = rightMatches.mostLeft()
        val rightMax = rightMatches.mostRight()

        // find repetitions with one separator
        val leftPossibles = leftMatches.findFullMatch(leftMin, leftMax)
        val rightPossibles = rightMatches.findFullMatch(rightMin, rightMax)
        return leftPossibles.flatMap { left -> rightPossibles.map { right -> left to right } }
            .filter { it.first.recognizer == it.second.recognizer }
            .filter { it.first.recognizer !is EchoRecognizer || (it.first.recognizer is EchoRecognizer && it.first.patterns[0] == it.second.patterns[0]) }
            .map { it.first }
    }

    private data class Distance(
        val first: Int,
        val last: Int
    ) {
        fun matchesInRange(matches: Collection<RecognizerMatch>) =
            matches.filter { it.ranges.size == 1 }
                .filter { it.ranges[0].first >= first && it.ranges[0].last <= last }

        companion object {
            fun between(predecessor: RecognizerMatch, successor: RecognizerMatch) =
                Distance(predecessor.last + 1, successor.first - 1)
        }
    }

    private data class Combination<P, C>(
        val leftIndex: Int,
        val rightIndex: Int,
        val leftParent: P,
        val rightParent: P,
        var element: C
    )

    private fun <X, C> List<X>.combine(combiner: (X, X) -> C) =
        (1 until size).map {
            val left = this[it - 1]
            val right = this[it]
            Combination(it - 1, it, left, right, combiner(left, right))

        }
}
