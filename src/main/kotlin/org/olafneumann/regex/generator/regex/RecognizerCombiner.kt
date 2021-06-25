package org.olafneumann.regex.generator.regex

object RecognizerCombiner {
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
                val rangeBetween =
                    IntRange(rangesToMatches[i - 1].range.last + 1, rangesToMatches[i].range.first - 1)
                if (!rangeBetween.isEmpty()) {
                    val part = if (options.onlyPatterns) {
                        RegularExpressionPart(rangeBetween, "*.")
                    } else {
                        val text = inputText.substring(rangeBetween)
                        RegularExpressionPart(rangeBetween, text.escapeForRegex(), text)
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


    private fun List<RegularExpressionPart>.addWholeLineMatchingStuff(options: Options): List<RegularExpressionPart> {
        return if (options.matchWholeLine) {
            listOf(
                RegularExpressionPart(IntRange.EMPTY, pattern = "^", title = "Start of input"),
                *toTypedArray(),
                RegularExpressionPart(IntRange.EMPTY, pattern = "$", title = "End of input")
            )
        } else {
            this
        }
    }

    private fun String.escapeForRegex() = PatternHelper.escapeForRegex(this)

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
        val originalText: String? = null,
        val match: RecognizerMatch? = null,
        val title: String? = null
    ) {
        val fromInputText
            get() = originalText != null
    }

    data class RegularExpression(
        val parts: Collection<RegularExpressionPart>
    ) {
        val pattern
            get() = parts.joinToString(separator = "") { it.pattern }
    }
}