package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import kotlin.browser.localStorage
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set

internal object ApplicationSettings {
    private const val KEY_CONSENT = "consent"
    private const val KEY_COMBINER_OPTIONS = "combiner.options"
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    // map to store settings while the user did not consent
    private val intermediate = mutableMapOf<String, String>()
    private fun persistIntermediate() {
        // a simple set will do, because now set() will actually write to localStorage
        intermediate.forEach { set(it.key, it.value) }
        intermediate.clear()
    }
    private fun loadIntermediateFromLocalStorage() {
        for (i in 0 until localStorage.length) {
            localStorage.key(i)?.let { key -> localStorage.getItem(key)?.let { intermediate[key] = it } }
        }
    }

    // Hiding local storage behind functions so we can disable storage if user does not consent
    private fun get(key: String) = intermediate[key] ?: localStorage.getItem(key)
    private fun set(key: String, value: String) {
        if (hasUserConsent) {
            localStorage.setItem(key, value)
        } else {
            intermediate[key] = value
        }
    }

    // conveniance
    private fun set(key: String, value: Int) = set(key, value.toString())
    private fun set(key: String, value: Boolean) = set(key, value.toString())

    // ----------------------------------
    // actual function to access settings

    fun isNewUser() = get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0 < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)

    var hasUserConsent: Boolean
        get() = get(KEY_CONSENT)?.toBoolean() ?: false
        set(value) {
            set(KEY_CONSENT, value)
            if (value) {
                persistIntermediate()
            } else {
                loadIntermediateFromLocalStorage()
            }
        }

    var viewOptions: RecognizerCombiner.Options
        get() = get(KEY_COMBINER_OPTIONS)?.let { JSON.parse<RecognizerCombiner.Options>(it) }
            ?: RecognizerCombiner.Options()
        set(value) = set(KEY_COMBINER_OPTIONS, JSON.stringify(value))

    private fun toLanguageExpandedProperty(language: String) = "language.$language.expanded"
    fun isLanguageExpanded(language: String) = get(toLanguageExpandedProperty(language))?.toBoolean() ?: false
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(toLanguageExpandedProperty(language), expanded)
}