package org.olafneumann.regex.generator.regex

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