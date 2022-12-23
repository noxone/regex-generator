package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.settings.ApplicationSettings
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.NPM
import org.olafneumann.regex.generator.ui.RGController


fun main() {
    window.onload = { initRegexGenerator() }
}

/* the caught exception is generic to really catch all exceptions */
@Suppress("TooGenericExceptionCaught")
private fun initRegexGenerator() {
    try {
        initRegexGeneratorUnsafe()
    } catch (exception: Exception) {
        exception.printStackTrace()
        window.alert("Unable to initialize RegexGenerator: ${exception.message}")
    }
}

private fun initRegexGeneratorUnsafe() {
    NPM.importAll()

    // initialize presentation code
    val controller = RGController()

    // store information, that we were already here
    val showGuide = ApplicationSettings.isNewUser()
    ApplicationSettings.storeUserLastInfo()

    controller.onFinishedLoading()

    // show guide for new users
    if (showGuide) {
        controller.onShowUserGuide(initialStep = true)
    }
}
