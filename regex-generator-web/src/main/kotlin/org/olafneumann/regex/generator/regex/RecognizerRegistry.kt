package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.regex.BracketedRecognizer.CenterPattern

object RecognizerRegistry {
    private val recognizers = listOf<Recognizer>(
        EchoRecognizer("Character", ".", priority = 1),
        SimpleRecognizer("One whitespace", "\\s"),
        SimpleRecognizer("Whitespaces", "\\s+"),
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

    private fun findRepetitionsPerGroup(
        allMatches: List<RecognizerMatch>,
        groupedMatches: List<RecognizerMatch>
    ): Collection<Recognizer> {
        val possibleRepetitions = groupedMatches.combine { left, right ->
            Distance.between(left, right).matchesInRange(allMatches)
        }.combine { left, right ->
            left.element.findSameMatchesWith((right.element))
        }

        //return emptyList()
        return possibleRepetitions
            .filter { it.element.isNotEmpty() }
            .map { combination ->
                val startMatch = combination.leftParent.leftParent
                val mainMatches = listOf(combination.element[0], combination.leftParent.rightParent)
                SimpleRecognizer(
                    name = "Combi [${startMatch.recognizer.name} + ${mainMatches[0].recognizer.name}]",
                    outputPattern = "(${startMatch.patterns[0]}(${mainMatches[0].patterns[0]}${mainMatches[1].patterns[0]})+)"
                )
            }
            .toSet()
    }

    private fun List<RecognizerMatch>.findSameMatchesWith(other: List<RecognizerMatch>): List<RecognizerMatch> {
        if (isEmpty() || other.isEmpty()) {
            return emptyList()
        }
        fun Collection<RecognizerMatch>.filterForRanges(count: Int) = filter { it.ranges.size == count }
        fun Collection<RecognizerMatch>.mostLeft() = flatMap { it.ranges }.map { it.first }.minOrNull() ?: Int.MIN_VALUE
        fun Collection<RecognizerMatch>.mostRight() = flatMap { it.ranges }.map { it.last }.maxOrNull() ?: Int.MAX_VALUE
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