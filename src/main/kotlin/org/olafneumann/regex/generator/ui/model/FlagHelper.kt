package org.olafneumann.regex.generator.ui.model

import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions

object FlagHelper {
    private const val CHAR_COMBINER_ONLY_PATTERNS = 'P'
    private const val CHAR_COMBINER_WHOLE_LINE = 'L'
    private const val CHAR_CODE_CASE_INSENSITIVE = 'i'
    private const val CHAR_CODE_DOT_MATCHES_LINE_BREAKS = 's'
    private const val CHAR_CODE_MULTILINE = 'm'

    fun parseCodeGeneratorOptions(regexFlags: String?): CodeGeneratorOptions {
        val caseInsensitive =
            regexFlags.parseFlag(
                char = CHAR_CODE_CASE_INSENSITIVE,
                default = CodeGeneratorOptions.DEFAULT_CASE_INSENSITIVE
            )
        val dotMatchesLineBreaks =
            regexFlags.parseFlag(
                char = CHAR_CODE_DOT_MATCHES_LINE_BREAKS,
                default = CodeGeneratorOptions.DEFAULT_DOT_MATCHES_LINE_BREAKS
            )
        val multiline =
            regexFlags.parseFlag(char = CHAR_CODE_MULTILINE, default = CodeGeneratorOptions.DEFAULT_MULTILINE)

        return CodeGeneratorOptions(
            caseInsensitive = caseInsensitive,
            dotMatchesLineBreaks = dotMatchesLineBreaks,
            multiline = multiline
        )
    }

    fun parseRecognizerMatchCombinerOptions(regexFlags: String?): RecognizerMatchCombinerOptions {
        val onlyPatterns = regexFlags.parseFlag(
            char = CHAR_COMBINER_ONLY_PATTERNS,
            default = RecognizerMatchCombinerOptions.DEFAULT_ONLY_PATTERN
        )
        val matchWholeLine = regexFlags.parseFlag(
            char = CHAR_COMBINER_WHOLE_LINE,
            default = RecognizerMatchCombinerOptions.DEFAULT_MATCH_WHOLE_LINE
        )

        return RecognizerMatchCombinerOptions(
            onlyPatterns = onlyPatterns,
            matchWholeLine = matchWholeLine
        )
    }

    fun getFlagString(
        codeGeneratorOptions: CodeGeneratorOptions,
        recognizerMatchCombinerOptions: RecognizerMatchCombinerOptions
    ): String = listOfNotNull(
        recognizerMatchCombinerOptions.onlyPatterns.ifTrue(CHAR_COMBINER_ONLY_PATTERNS),
        recognizerMatchCombinerOptions.matchWholeLine.ifTrue(CHAR_COMBINER_WHOLE_LINE),
        codeGeneratorOptions.caseInsensitive.ifTrue(CHAR_CODE_CASE_INSENSITIVE),
        codeGeneratorOptions.dotMatchesLineBreaks.ifTrue(CHAR_CODE_DOT_MATCHES_LINE_BREAKS),
        codeGeneratorOptions.multiline.ifTrue(CHAR_CODE_MULTILINE)
    )
        .joinToString(separator = "") { it.toString() }


    private fun Boolean.ifTrue(char: Char): Char? =
        if (this) char else null


    private fun String?.parseFlag(char: Char, default: Boolean): Boolean =
        this?.contains(char = char, ignoreCase = false) ?: default
}