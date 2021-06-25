package org.olafneumann.regex.generator.regex

class RecognizerCombiner {
    companion object {
        private fun makeOutput(hasLength: Boolean, options: Options, outputProvider: () -> String) =
            when {
                hasLength && options.onlyPatterns && options.matchWholeLine -> ".*"
                hasLength && !options.onlyPatterns -> outputProvider()
                else -> ""
            }

        fun combine(
            inputText: String,
            selectedMatches: Collection<RecognizerMatch>,
            options: Options
        ): RegularExpression {
            val rangesToMatches = selectedMatches.flatMap { match ->
                    match.ranges
                        .mapIndexed { index, range -> RegularExpressionPart(range, match.patterns[index]) }
                }
                .sortedBy { it.range.first }
                .toList()

            val first = makeOutput(rangesToMatches.isNotEmpty() && rangesToMatches.first().range.first > 0, options) {
                inputText.substring(0, rangesToMatches.first().range.first).escapeForRegex()
            }
            val last = makeOutput(
                rangesToMatches.isNotEmpty() && rangesToMatches.last().range.last < inputText.length - 1,
                options
            ) {
                inputText.substring(rangesToMatches.last().range.last + 1).escapeForRegex()
            }

            val parts = mutableListOf<RegularExpressionPart>()
            val pattern = buildString {
                append(first)
                rangesToMatches.ifEmpty {
                    append(
                        if (options.onlyPatterns) {
                            ".*"
                        } else {
                            inputText.escapeForRegex()
                        }
                    )
                }
                for (i in rangesToMatches.indices) {
                    if (i > 0) {
                        val range = IntRange(rangesToMatches[i - 1].range.last + 1, rangesToMatches[i].range.first - 1)
                        if (options.onlyPatterns) {
                            append(
                                if (range.isEmpty()) {
                                    ""
                                } else {
                                    ".*"
                                }
                            )
                        } else {
                            append(inputText.substring(range).escapeForRegex())
                        }
                    }
                    append(rangesToMatches[i].pattern)
                }
                append(last)
            }

            return RegularExpression(options.getFrame().format(pattern))
        }
        
        private fun String.escapeForRegex() = PatternHelper.escapeForRegex(this)
    }

    data class Options(
        val onlyPatterns: Boolean = false,
        val matchWholeLine: Boolean = true,
        val caseSensitive: Boolean = true,
        val dotMatchesLineBreaks: Boolean = false,
        val multiline: Boolean = false
    ) {
        internal fun getFrame() = if (matchWholeLine) {
            Frame("^", "$")
        } else {
            Frame("", "")
        }
    }

    internal data class Frame(
        val start: String,
        val end: String
    ) {
        internal fun format(pattern: String) = "$start$pattern$end"
    }

    data class RegularExpressionPart(
        val range: IntRange,
        val pattern: String,
        val match: RecognizerMatch?
    )

    data class RegularExpression(
        val parts: MutableCollection<RegularExpressionPart>
    )
}