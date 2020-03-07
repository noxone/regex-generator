package org.olafneumann.regex.generator.ui

import org.w3c.dom.get
import kotlin.browser.localStorage

internal object ApplicationSettings {
    private const val KEY_LAST_VERSION = "user.lastVersion"
    private const val VAL_VERSION = 3

    /* Hiding local storage behind functions so we can disable storage if user does not consent */
    private fun get(key: String) = localStorage.getItem(key)
    private fun set(key: String, value: String) = localStorage.setItem(key, value)
    private fun set(key: String, value: Int) = set(key, value.toString())

    /* actual function to access settings */
    fun isNewUser() = get(KEY_LAST_VERSION)?.toIntOrNull() ?: 0 < VAL_VERSION
    fun storeUserLastInfo() = set(KEY_LAST_VERSION, VAL_VERSION)


}