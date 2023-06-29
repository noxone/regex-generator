package org.olafneumann.regex.generator

import kotlinx.browser.document
import org.olafneumann.regex.generator.settings.ApplicationSettings
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.Clarity
import org.olafneumann.regex.generator.js.NPM
import org.olafneumann.regex.generator.ui.RGController


const val FEATURE_FLAG_CAPTURING_GROUPS = false
const val FEATURE_FLAG_PATTERN_CASE_MODIFIER = true
private const val CLARITY_DELAY = 2000

fun main() {
    window.onload = { initRegexGenerator() }
}

/* the caught exception is generic to really catch all exceptions */
@Suppress("TooGenericExceptionCaught")
private fun initRegexGenerator() {
    // registerErrorHandler()
    initRegexGeneratorUnsafe()
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

    // report commit ID
    document.body
        ?.getAttribute("rg-commit-id")
        ?.let {
            console.info("commit-id", it)
            window.setTimeout({
                Clarity.set("commit-id", it)
            }, CLARITY_DELAY)
        }
}
