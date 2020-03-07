package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import kotlin.browser.localStorage

internal object ApplicationSettings {
    private const val KEY_CONSENT = "consent"
    private const val KEY_COMBINER_OPTIONS = "combiner.options"
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    /* Hiding local storage behind functions so we can disable storage if user does not consent */
    private fun get(key: String) = localStorage.getItem(key)
    private fun set(key: String, value: String) = localStorage.setItem(key, value)
    private fun set(key: String, value: Int) = set(key, value.toString())
    private fun set(key: String, value: Boolean) = set(key, value.toString())

    /* actual function to access settings */
    fun isNewUser() = get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0 < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)

    var hasConsent: Boolean
        get() = get(KEY_CONSENT)?.toBoolean() ?: false
        set(value) = set(KEY_CONSENT, value)

    var viewOptions: RecognizerCombiner.Options
        get() = get(KEY_COMBINER_OPTIONS)?.let { JSON.parse<RecognizerCombiner.Options>(it) }
            ?: RecognizerCombiner.Options()
        set(value) = set(KEY_COMBINER_OPTIONS, JSON.stringify(value))

    private fun toLanguageExpandedProperty(language: String) = "language.$language.expanded"
    fun isLanguageExpanded(language: String) = get(toLanguageExpandedProperty(language))?.toBoolean() ?: false
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(toLanguageExpandedProperty(language), expanded)
}