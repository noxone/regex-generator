package org.olafneumann.regex.generator

import kotlinx.browser.document
import org.olafneumann.regex.generator.settings.ApplicationSettings
import kotlinx.browser.window
import kotlinx.html.js.a
import kotlinx.html.js.p
import kotlinx.html.js.pre
import org.olafneumann.regex.generator.js.NPM
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.ui.RGController
import org.olafneumann.regex.generator.ui.components.Button
import org.olafneumann.regex.generator.ui.components.createModal


fun main() {
    window.onload = { initRegexGenerator() }
}

/* the caught exception is generic to really catch all exceptions */
@Suppress("TooGenericExceptionCaught")
private fun initRegexGenerator() {
    try {
        initRegexGeneratorUnsafe()
    } catch (throwable: Throwable) {
        console.error(throwable.stackTraceToString())
        showErrorMessage(throwable)
    }
}

private val gitCommitId: String get() = document.body?.getAttribute("rg-commit-id") ?: ""

private fun showErrorMessage(throwable: Throwable) {
    val textToCopy = """Exception: ${throwable.message}
GIT Commit ID: $gitCommitId
UserAgent: ${window.navigator.userAgent}
Vendor: ${window.navigator.vendor}
Language: ${window.navigator.language}
Platform: ${window.navigator.platform}
CPU: ${window.navigator.oscpu}
TouchPoints: ${window.navigator.maxTouchPoints}

StackTrace:
${throwable.stackTraceToString()}"""
    createModal(
        title = "Houston, we have a problem!",
        buttons = listOf(
            Button(title = "Copy text", colorClass = "secondary") { copyToClipboard(textToCopy) },
            Button(title = "Close") { hide() }
        ),
    ) {
        p {
            +"An error occurred while loading regex generator. This should not happen."
        }
        p {
            +"Please open an "
            a {
                href = "https://github.com/noxone/regex-generator/issues/new" +
                        "?assignees=&labels=&template=bug_report.md&title="
                target = "_blank"
                rel = "noopener"
                +"issue"
            }
            +" for this on github and add the following information:"
        }
        pre(classes = "border rounded p-2 user-select-all") {
            +textToCopy
        }
    }
        .show()
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
