package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.recognizer.escapeForRegex

object RegexMatchCombiner {
    fun combineMatches(
        inputText: String,
        selectedMatches: Collection<RecognizerMatch>,
        options: Options
    ): RegularExpression {
        val rangesToMatches = selectedMatches.flatMap { match ->
            match.ranges
                .mapIndexed { index, range -> RegularExpressionPart(range, match.patterns[index], match = match) }
        }
            .sortedBy { it.range.first }
            .toList()

        return if (rangesToMatches.isEmpty()) {
            if (options.onlyPatterns) {
                RegularExpression(
                    listOf(RegularExpressionPart(IntRange(0, 2), ".*", title = "anything"))
                        .addWholeLineMatchingStuff(options)
                )
            } else {
                val regex = inputText.escapeForRegex()
                RegularExpression(
                    listOf(RegularExpressionPart(IntRange(0, regex.length), regex, inputText))
                        .addWholeLineMatchingStuff(options)
                )
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
        options: Options
    ): RegularExpression {
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

        return RegularExpression(parts.addWholeLineMatchingStuff(options))
    }

    private fun getPartBetween(
        inputText: String,
        first: RegularExpressionPart,
        second: RegularExpressionPart,
        options: Options
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

    private fun List<RegularExpressionPart>.addWholeLineMatchingStuff(options: Options): List<RegularExpressionPart> {
        return if (options.matchWholeLine) {
            val list = mutableListOf<RegularExpressionPart>()
            list.add(RegularExpressionPart(IntRange.EMPTY, pattern = "^", title = "Start of input"))
            list.addAll(this)
            list.add(RegularExpressionPart(IntRange.EMPTY, pattern = "$", title = "End of input"))
            list
        } else {
            this
        }
    }

    data class Options(
        val onlyPatterns: Boolean = DEFAULT_ONLY_PATTERN,
        val matchWholeLine: Boolean = DEFAULT_MATCH_WHOLE_LINE,
        val caseInsensitive: Boolean = DEFAULT_CASE_INSENSITIVE,
        val dotMatchesLineBreaks: Boolean = DEFAULT_DOT_MATCHES_LINE_BREAKS,
        val multiline: Boolean = DEFAULT_MULTILINE
    ) {
        val flagString: String
            get() = listOfNotNull(
                    onlyPatterns.ifTrue(CHAR_ONLY_PATTERNS),
                    matchWholeLine.ifTrue(CHAR_WHOLE_LINE),
                    caseInsensitive.ifTrue(CHAR_CASE_INSENSITIVE),
                    dotMatchesLineBreaks.ifTrue(CHAR_DOT_MATCHES_LINE_BREAKS),
                    multiline.ifTrue(CHAR_MULTILINE)
                )
                .joinToString(separator = "") { it.toString() }

        companion object {
            private const val DEFAULT_ONLY_PATTERN = false
            private const val DEFAULT_MATCH_WHOLE_LINE = false
            private const val DEFAULT_CASE_INSENSITIVE = true
            private const val DEFAULT_DOT_MATCHES_LINE_BREAKS = false
            private const val DEFAULT_MULTILINE = false

            const val CHAR_ONLY_PATTERNS = 'P'
            const val CHAR_WHOLE_LINE = 'L'
            const val CHAR_CASE_INSENSITIVE = 'i'
            const val CHAR_DOT_MATCHES_LINE_BREAKS = 's'
            const val CHAR_MULTILINE = 'm'

            private fun Boolean.ifTrue(char: Char): Char? =
                if (this) char else null

            fun parseSearchParams(regexFlags: String?): Options {
                val onlyPatterns = regexFlags.parseFlag(char = CHAR_ONLY_PATTERNS, default = DEFAULT_ONLY_PATTERN)
                val matchWholeLine = regexFlags.parseFlag(char = CHAR_WHOLE_LINE, default = DEFAULT_MATCH_WHOLE_LINE)
                val caseInsensitive =
                    regexFlags.parseFlag(char = CHAR_CASE_INSENSITIVE, default = DEFAULT_CASE_INSENSITIVE)
                val dotMatchesLineBreaks =
                    regexFlags.parseFlag(char = CHAR_DOT_MATCHES_LINE_BREAKS, default = DEFAULT_DOT_MATCHES_LINE_BREAKS)
                val multiline = regexFlags.parseFlag(char = CHAR_MULTILINE, default = DEFAULT_MULTILINE)
                return Options(
                    onlyPatterns = onlyPatterns,
                    matchWholeLine = matchWholeLine,
                    caseInsensitive = caseInsensitive,
                    dotMatchesLineBreaks = dotMatchesLineBreaks,
                    multiline = multiline
                )
            }

            private fun String?.parseFlag(char: Char, default: Boolean): Boolean =
                this?.contains(char = char, ignoreCase = false) ?: default
        }
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
