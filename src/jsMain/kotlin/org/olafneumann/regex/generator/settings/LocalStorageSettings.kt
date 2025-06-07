package org.olafneumann.regex.generator.settings

import kotlinx.browser.localStorage
import org.olafneumann.regex.generator.js.Clarity

internal open class LocalStorageSettings {
    companion object {
        private const val KEY_CONSENT = "consent"
    }

    init {
        if (hasUserConsent) {
            onConsentGiven()
        }
    }

    // Hiding local storage behind functions, so we can disable storage if user does not consent
    protected fun get(key: String) = localStorage.getItem(key)
    protected fun get(key: String, default: Boolean): Boolean = get(key)?.toBoolean() ?: default
    protected fun get(key: String, default: Int): Int = get(key)?.toIntOrNull() ?: default
    protected fun set(key: String, value: String) = localStorage.setItem(key, value)
    protected fun remove(key: String) = localStorage.removeItem(key = key)

    // convenience
    protected fun set(key: String, value: Int) = set(key, value.toString())
    protected fun set(key: String, value: Boolean) = set(key, value.toString())

    var hasUserConsent: Boolean
        get() = get(KEY_CONSENT, false)
        set(value) {
            set(KEY_CONSENT, value)
            if (value) {
                onConsentGiven()
            }
        }

    private fun onConsentGiven() {
        Clarity.consent()
    }
}
