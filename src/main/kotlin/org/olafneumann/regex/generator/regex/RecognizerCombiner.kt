package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.regex.RecognizerCombiner.Companion.escapeForRegex

class RecognizerCombiner {
    companion object {
        private fun makeOutput(hasLength: Boolean, options: Options, outputProvider: () -> String) =
            when {
                hasLength && options.onlyPatterns && options.matchWholeLine -> ".*"
                hasLength && !options.onlyPatterns -> outputProvider()
                else -> ""
            }

        fun combineMatches(
            inputText: String,
            selectedMatches: Collection<RecognizerMatch>,
            options: Options
        ): RegularExpression {
            val rangesToMatches = selectedMatches.flatMap { match ->
                    match.ranges
                        .mapIndexed { index, range -> RegularExpressionPart(range, match.patterns[index], fromInputText = false) }
                }
                .sortedBy { it.range.first }
                .toList()

            return if (rangesToMatches.isEmpty()) {
                if (options.onlyPatterns) {
                    RegularExpression(mutableListOf(RegularExpressionPart(IntRange(0,2), ".*", fromInputText = false)).addWholeLineMatchingStuff(options))
                } else {
                    val regex = inputText.escapeForRegex()
                    RegularExpression(listOf(RegularExpressionPart(IntRange(0,regex.length), regex, fromInputText = true)).addWholeLineMatchingStuff(options))
                }
            } else {
                // at this point we know, that rangesToMatches is not empty!
                val hasContentBeforeFirstMatch = rangesToMatches.first().range.first > 0
                val hasContentAfterLastMatch = rangesToMatches.last().range.last < inputText.length - 1

                val firstPart = when {
                    hasContentBeforeFirstMatch && options.onlyPatterns -> RegularExpressionPart(IntRange(0, rangesToMatches.first().range.first - 1), ".*", fromInputText = false)
                    hasContentBeforeFirstMatch && !options.onlyPatterns -> RegularExpressionPart(IntRange(0, rangesToMatches.first().range.first - 1), inputText.substring(0, rangesToMatches.first().range.first).escapeForRegex(), fromInputText = true)
                    else -> null
                }
                val lastPart = when {
                    hasContentAfterLastMatch && options.onlyPatterns -> RegularExpressionPart(IntRange(0, rangesToMatches.last().range.last), ".*", fromInputText = false)
                    hasContentAfterLastMatch && !options.onlyPatterns -> RegularExpressionPart(IntRange(0, rangesToMatches.last().range.last), inputText.substring(rangesToMatches.last().range.last + 1).escapeForRegex(), fromInputText = true)
                    else -> null
                }

                combineParts(firstPart, rangesToMatches, lastPart, inputText, options)
            }
        }

        private fun List<RegularExpressionPart>.addWholeLineMatchingStuff(options: Options): List<RegularExpressionPart> {
            return if (options.matchWholeLine) {
                listOf(RegularExpressionPart(IntRange.EMPTY, pattern = "^", fromInputText = false), *toTypedArray(), RegularExpressionPart(IntRange.EMPTY, pattern = "$", fromInputText = false))
            } else {
                this
            }
        }

        private fun combineParts(firstPart: RegularExpressionPart?, rangesToMatches: List<RegularExpressionPart>, lastPart: RegularExpressionPart?, inputText: String, options: Options): RegularExpression {
            val parts = mutableListOf<RegularExpressionPart>()
            if (options.matchWholeLine || firstPart?.fromInputText == true) {
                firstPart?.let { parts.add(it) }
            }

            for (i in rangesToMatches.indices) {
                if (i > 0) {
                    val rangeBetween = IntRange(rangesToMatches[i - 1].range.last + 1, rangesToMatches[i].range.first - 1)
                    if (!rangeBetween.isEmpty()) {
                        val part = if (options.onlyPatterns) {
                            RegularExpressionPart(rangeBetween, "*.", fromInputText = false)
                        } else {
                            RegularExpressionPart(rangeBetween, inputText.substring(rangeBetween).escapeForRegex(), fromInputText = true)
                        }
                        parts.add(part)
                    }
                }
                parts.add(rangesToMatches[i])
            }

            if (options.matchWholeLine || lastPart?.fromInputText == true) {
                lastPart?.let { parts.add(it) }
            }

            return RegularExpression(parts.addWholeLineMatchingStuff(options))
        }

        private fun String.escapeForRegex() = PatternHelper.escapeForRegex(this)
    }

    data class Options(
        val onlyPatterns: Boolean = false,
        val matchWholeLine: Boolean = true,
        val caseSensitive: Boolean = true,
        val dotMatchesLineBreaks: Boolean = false,
        val multiline: Boolean = false
    )

    data class RegularExpressionPart(
        val range: IntRange,
        val pattern: String,
        val fromInputText: Boolean,
        val match: RecognizerMatch? = null
    )

    data class RegularExpression(
        val parts: Collection<RegularExpressionPart>
    ) {
        val pattern
            get() = parts.joinToString(separator = "") { it.pattern }
    }
}