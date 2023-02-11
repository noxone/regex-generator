package org.olafneumann.regex.generator.settings

import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions

internal object ApplicationSettings : LocalStorageSettings() {
    private const val KEY_RECOGNIZER_COMBINER_OPTIONS = "recognizer.combiner.options"
    private const val KEY_CODE_GENERATOR_OPTIONS = "code.generator.options"
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    // ----------------------------------
    // actual function to access settings

    fun isNewUser() = (get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0) < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)

    var codeGeneratorOptions: CodeGeneratorOptions
        get() = get(KEY_CODE_GENERATOR_OPTIONS)?.let { JSON.parse<CodeGeneratorOptions>(it) } ?: CodeGeneratorOptions()
        set(value) = set(KEY_CODE_GENERATOR_OPTIONS, JSON.stringify(value))

    var recognizerMatchCombinerOptions: RecognizerMatchCombinerOptions
        get() = get(KEY_RECOGNIZER_COMBINER_OPTIONS)?.let { JSON.parse<RecognizerMatchCombinerOptions>(it) }
            ?: RecognizerMatchCombinerOptions()
        set(value) = set(KEY_RECOGNIZER_COMBINER_OPTIONS, JSON.stringify(value))

    private fun String.toLanguageExpandedProperty() = "language.${this}.expanded"
    fun isLanguageExpanded(language: String) = get(language.toLanguageExpandedProperty())?.toBoolean() ?: false
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(language.toLanguageExpandedProperty(), expanded)
}
