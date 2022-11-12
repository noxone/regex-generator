package org.olafneumann.regex.generator.settings

import org.olafneumann.regex.generator.regex.RegexMatchCombiner

internal object ApplicationSettings : LocalStorageSettings() {
    private const val KEY_COMBINER_OPTIONS = "combiner.options"
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    // ----------------------------------
    // actual function to access settings

    fun isNewUser() = (get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0) < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)


    var regexCombinerOptions: RegexMatchCombiner.Options
        get() = get(KEY_COMBINER_OPTIONS)?.let { JSON.parse<RegexMatchCombiner.Options>(it) }
            ?: RegexMatchCombiner.Options()
        set(value) = set(KEY_COMBINER_OPTIONS, JSON.stringify(value))

    private fun String.toLanguageExpandedProperty() = "language.${this}.expanded"
    fun isLanguageExpanded(language: String) = get(language.toLanguageExpandedProperty())?.toBoolean() ?: false
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(language.toLanguageExpandedProperty(), expanded)
}
