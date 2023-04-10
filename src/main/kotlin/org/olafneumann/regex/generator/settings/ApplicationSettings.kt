package org.olafneumann.regex.generator.settings

import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions

internal object ApplicationSettings : LocalStorageSettings() {
    private const val KEY_RECOGNIZER_COMBINER_OPTIONS = "recognizer.combiner.options"
    private const val KEY_RCO_ONLY_PATTERNS = "$KEY_RECOGNIZER_COMBINER_OPTIONS.onlyPatterns"
    private const val KEY_RCO_MATCH_WHOLE_LINE = "$KEY_RECOGNIZER_COMBINER_OPTIONS.matchWholeLine"
    private const val KEY_RCO_GENERATE_LOWER_CASE = "$KEY_RECOGNIZER_COMBINER_OPTIONS.generateLowerCase"

    private const val KEY_CODE_GENERATOR_OPTIONS = "code.generator.options"
    private const val KEY_CGO_CASE_INSENSITIVE = "$KEY_CODE_GENERATOR_OPTIONS.caseInsensitive"
    private const val KEY_CGO_DOT_MATCHES_LINE_BREAKS = "$KEY_CODE_GENERATOR_OPTIONS.dotMatchesLineBreaks"
    private const val KEY_CGO_MULTILINE = "$KEY_CODE_GENERATOR_OPTIONS.multiline"

    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    // ----------------------------------
    // actual function to access settings

    fun isNewUser() = get(KEY_LAST_VERSION, 0) < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)

    var codeGeneratorOptions: CodeGeneratorOptions
        get() {
            val caseInsensitive = get(KEY_CGO_CASE_INSENSITIVE, CodeGeneratorOptions.DEFAULT_CASE_INSENSITIVE)
            val dotMatchesLineBreaks = get(KEY_CGO_DOT_MATCHES_LINE_BREAKS, CodeGeneratorOptions.DEFAULT_DOT_MATCHES_LINE_BREAKS)
            val multiline = get(KEY_CGO_MULTILINE, CodeGeneratorOptions.DEFAULT_MULTILINE)
            return CodeGeneratorOptions(
                caseInsensitive = caseInsensitive,
                dotMatchesLineBreaks = dotMatchesLineBreaks,
                multiline = multiline
            )
        }
        set(value) {
            set(KEY_CGO_CASE_INSENSITIVE, value.caseInsensitive)
            set(KEY_CGO_DOT_MATCHES_LINE_BREAKS, value.dotMatchesLineBreaks)
            set(KEY_CGO_MULTILINE, value.multiline)
        }

    var recognizerMatchCombinerOptions: RecognizerMatchCombinerOptions
        @Suppress("TooGenericExceptionCaught")
        get() {
            val onlyPatterns = get(KEY_RCO_ONLY_PATTERNS, RecognizerMatchCombinerOptions.DEFAULT_ONLY_PATTERN)
            val matchWholeLine = get(KEY_RCO_MATCH_WHOLE_LINE, RecognizerMatchCombinerOptions.DEFAULT_MATCH_WHOLE_LINE)
            val generateLowerCase = get(KEY_RCO_GENERATE_LOWER_CASE, RecognizerMatchCombinerOptions.DEFAULT_GENERATE_LOWER_CASE)
            return RecognizerMatchCombinerOptions(
                onlyPatterns = onlyPatterns,
                matchWholeLine = matchWholeLine,
                generateLowerCase = generateLowerCase
            )
        }
        set(value) {
            set(KEY_RCO_ONLY_PATTERNS, value.onlyPatterns)
            set(KEY_RCO_MATCH_WHOLE_LINE, value.matchWholeLine)
            set(KEY_RCO_GENERATE_LOWER_CASE, value.generateLowerCase)
        }

    private fun String.toLanguageExpandedProperty() = "language.${this}.expanded"
    fun isLanguageExpanded(language: String) = get(language.toLanguageExpandedProperty(), false)
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(language.toLanguageExpandedProperty(), expanded)
}
