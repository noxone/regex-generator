package org.olafneumann.regex.generator

import org.olafneumann.regex.generator.settings.ApplicationSettings
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.NPM
import org.olafneumann.regex.generator.ui.RGController


fun main() {
    window.onload = { initRegexGenerator() }
}

// TODO add service worker:
// https://resources.jetbrains.com/storage/products/kotlinconf2019/slides/Thursday/Aud%2012/E.%20Hellman,%20Building%20Progressive%20Web%20Apps%20%20in%20Kotlin.pdf
// PWA STUFF
// https://web.dev/app-like-pwas/

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
