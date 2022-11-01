package org.olafneumann.regex.generator.settings

import kotlinx.browser.localStorage

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
