package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.ui.UiController
import org.w3c.dom.set
import kotlin.browser.localStorage
import kotlin.browser.window

const val KEY_LAST_VERSION = "user.lastVersion"
const val KEY_LAST_VISIT = "user.lastVisit"

const val VAL_VERSION = 3
const val VAL_EXAMPLE_INPUT = "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."

fun main() = initRegexGenerator()

private fun initRegexGenerator() {
    try {
        // initialize presentation code
        val presenter = UiController()
        val input = if (presenter.currentTextInput.isBlank()) VAL_EXAMPLE_INPUT else presenter.currentTextInput
        presenter.recognizeMatches(input)

        // store information, that we were already here
        val showGuide = isNewUser()
        storeUserLastInfo()

        // show guide for new users
        if (showGuide) {
            presenter.showInitialUserGuide()
        }
    } catch (exception: Exception) {
        console.error(exception)
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}

private fun isNewUser(): Boolean =
    localStorage.getItem(KEY_LAST_VISIT)?.toBoolean() ?: true
            || localStorage.getItem(KEY_LAST_VERSION)?.toIntOrNull() ?: VAL_VERSION < VAL_VERSION
private fun storeUserLastInfo() {
    localStorage[KEY_LAST_VERSION] = VAL_VERSION.toString()
}


