package org.olafneumann.regex.generator.regex

data class RecognizerMatchCombinerOptions(
    val onlyPatterns: Boolean = DEFAULT_ONLY_PATTERN,
    val matchWholeLine: Boolean = DEFAULT_MATCH_WHOLE_LINE,
) {
    companion object {
        const val DEFAULT_ONLY_PATTERN = false
        const val DEFAULT_MATCH_WHOLE_LINE = false
    }
}
