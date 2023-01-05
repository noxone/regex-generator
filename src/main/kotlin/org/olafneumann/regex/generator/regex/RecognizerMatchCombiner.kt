package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.recognizer.PatternCaseModifier
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.recognizer.escapeForRegex

object RecognizerMatchCombiner {
    fun combineMatches(
        inputText: String,
        selectedMatches: Collection<RecognizerMatch>,
        options: RecognizerMatchCombinerOptions
    ): CombinedRegex {
        val rangesToMatches = selectedMatches.flatMap { match ->
            match.ranges
                .mapIndexed { index, range ->
                    RegularExpressionPart(
                        range,
                        PatternCaseModifier.generateOutputPattern(match.patterns[index], options.case),
                        match = match
                    )
                }
        }
            .sortedBy { it.range.first }
            .toList()

        return if (rangesToMatches.isEmpty()) {
            if (options.onlyPatterns) {
                val parts = mutableListOf(RegularExpressionPart(IntRange(0, 2), ".*", title = "anything"))
                addWholeLineMatchingStuff(parts = parts, options = options)
                CombinedRegex(parts)
            } else {
                val regex = inputText.escapeForRegex()
                val parts = mutableListOf(RegularExpressionPart(IntRange(0, regex.length), regex, inputText))
                addWholeLineMatchingStuff(parts = parts, options = options)
                CombinedRegex(parts)
            }
        } else {
            // at this point we know, that rangesToMatches is not empty!
            val hasContentBeforeFirstMatch = rangesToMatches.first().range.first > 0
            val hasContentAfterLastMatch = rangesToMatches.last().range.last < inputText.length - 1


            val firstPart = when {
                hasContentBeforeFirstMatch && options.onlyPatterns -> RegularExpressionPart(
                    IntRange(0, rangesToMatches.first().range.first - 1), ".*", title = "anything"
                )

                hasContentBeforeFirstMatch && !options.onlyPatterns -> {
                    val length = rangesToMatches.first().range.first
                    val text = inputText.substring(0, length)
                    RegularExpressionPart(IntRange(0, length - 1), text.escapeForRegex(), text)
                }

                else -> null
            }
            val lastPart = when {
                hasContentAfterLastMatch && options.onlyPatterns -> RegularExpressionPart(
                    IntRange(0, rangesToMatches.last().range.last), ".*", title = "anything"
                )

                hasContentAfterLastMatch && !options.onlyPatterns -> {
                    val text = inputText.substring(rangesToMatches.last().range.last + 1)
                    RegularExpressionPart(IntRange(0, rangesToMatches.last().range.last), text.escapeForRegex(), text)
                }

                else -> null
            }

            combineParts(firstPart, rangesToMatches, lastPart, inputText, options)
        }
    }

    private fun combineParts(
        firstPart: RegularExpressionPart?,
        rangesToMatches: List<RegularExpressionPart>,
        lastPart: RegularExpressionPart?,
        inputText: String,
        options: RecognizerMatchCombinerOptions
    ): CombinedRegex {
        val parts = mutableListOf<RegularExpressionPart>()
        if (options.matchWholeLine || firstPart?.fromInputText == true) {
            firstPart?.let { parts.add(it) }
        }

        for (i in rangesToMatches.indices) {
            if (i > 0) {
                getPartBetween(inputText, rangesToMatches[i - 1], rangesToMatches[i], options)
                    ?.let { parts.add(it) }
            }
            parts.add(rangesToMatches[i])
        }

        if (options.matchWholeLine || lastPart?.fromInputText == true) {
            lastPart?.let { parts.add(it) }
        }

        addWholeLineMatchingStuff(parts = parts, options = options)

        return CombinedRegex(parts)
    }

    private fun getPartBetween(
        inputText: String,
        first: RegularExpressionPart,
        second: RegularExpressionPart,
        options: RecognizerMatchCombinerOptions
    ): RegularExpressionPart? {
        val rangeBetween = IntRange(first.range.last + 1, second.range.first - 1)
        if (!rangeBetween.isEmpty()) {
            return if (options.onlyPatterns) {
                RegularExpressionPart(rangeBetween, ".*")
            } else {
                val text = inputText.substring(rangeBetween)
                RegularExpressionPart(rangeBetween, text.escapeForRegex(), text)
            }
        }
        return null
    }

    private fun addWholeLineMatchingStuff(
        parts: MutableList<RegularExpressionPart>,
        options: RecognizerMatchCombinerOptions
    ) {
        if (!options.matchWholeLine) {
            return
        }

        parts.add(0, RegularExpressionPart(IntRange.EMPTY, pattern = "^", title = "Start of input"))
        parts.add(RegularExpressionPart(IntRange.EMPTY, pattern = "$", title = "End of input"))
    }

    data class RegularExpressionPart(
        val range: IntRange,
        val pattern: String,
        val originalText: String? = null,
        val match: RecognizerMatch? = null,
        val title: String? = null
    ) {
        val fromInputText
            get() = originalText != null
    }
}
