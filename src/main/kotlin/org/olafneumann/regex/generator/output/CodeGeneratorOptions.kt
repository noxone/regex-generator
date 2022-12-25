package org.olafneumann.regex.generator.output

data class CodeGeneratorOptions(
    val caseInsensitive: Boolean = DEFAULT_CASE_INSENSITIVE,
    val dotMatchesLineBreaks: Boolean = DEFAULT_DOT_MATCHES_LINE_BREAKS,
    val multiline: Boolean = DEFAULT_MULTILINE
) {
    companion object {
        const val DEFAULT_CASE_INSENSITIVE = true
        const val DEFAULT_DOT_MATCHES_LINE_BREAKS = false
        const val DEFAULT_MULTILINE = false
    }
}