package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import kotlinx.browser.localStorage
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set

internal open class LocalStorageSettings {
    companion object {
        private const val KEY_CONSENT = "consent"
    }

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

    init {
        if (!hasUserConsent) {
            loadIntermediateFromLocalStorage()
            localStorage.clear()
        }
    }

    // Hiding local storage behind functions, so we can disable storage if user does not consent
    protected fun get(key: String) = intermediate[key] ?: localStorage.getItem(key)
    protected fun set(key: String, value: String) {
        if (hasUserConsent) {
            localStorage.setItem(key, value)
        } else {
            intermediate[key] = value
        }
    }

    // convenience
    protected fun set(key: String, value: Int) = set(key, value.toString())
    protected fun set(key: String, value: Boolean) = set(key, value.toString())

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
}

internal object ApplicationSettings : LocalStorageSettings() {
    private const val KEY_COMBINER_OPTIONS = "combiner.options"
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    // ----------------------------------
    // actual function to access settings

    fun isNewUser() = (get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0) < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)


    var viewOptions: RecognizerCombiner.Options
        get() = get(KEY_COMBINER_OPTIONS)?.let { JSON.parse<RecognizerCombiner.Options>(it) }
            ?: RecognizerCombiner.Options()
        set(value) = set(KEY_COMBINER_OPTIONS, JSON.stringify(value))

    private fun String.toLanguageExpandedProperty() = "language.${this}.expanded"
    fun isLanguageExpanded(language: String) = get(language.toLanguageExpandedProperty())?.toBoolean() ?: false
    fun setLanguageExpanded(language: String, expanded: Boolean) = set(language.toLanguageExpandedProperty(), expanded)
}
