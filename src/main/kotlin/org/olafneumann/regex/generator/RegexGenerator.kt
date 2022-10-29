package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.settings.ApplicationSettings
import org.olafneumann.regex.generator.ui.UiController
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.NPM


fun main() {
    window.onload = { initRegexGenerator() }
}

/* the caught exception is generic to really catch all exceptions */
@Suppress("TooGenericExceptionCaught")
private fun initRegexGenerator() {
    try {
        initRegexGeneratorUnsafe()
    } catch (exception: Exception) {
        console.error(exception)
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}

private fun initRegexGeneratorUnsafe() {
    NPM.importAll()

    // initialize presentation code
    val presenter = UiController()

    // store information, that we were already here
    val showGuide = ApplicationSettings.isNewUser()
    ApplicationSettings.storeUserLastInfo()

    // show guide for new users
    if (showGuide) {
        presenter.showInitialUserGuide()
    }
}
