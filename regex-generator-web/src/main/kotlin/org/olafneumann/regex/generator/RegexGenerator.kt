package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.ui.ApplicationSettings
import org.olafneumann.regex.generator.ui.UiController
import org.w3c.dom.set
import kotlin.browser.localStorage
import kotlin.browser.window

fun main() {
    try {
        initRegexGenerator()
    } catch (exception: Exception) {
        console.error(exception)
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}

private fun initRegexGenerator() {
    // initialize presentation code
    val presenter = UiController()
    presenter.initialize()

    // store information, that we were already here
    val showGuide = ApplicationSettings.isNewUser()
    ApplicationSettings.storeUserLastInfo()

    // show guide for new users
    if (showGuide) {
        presenter.showInitialUserGuide()
    }
}
