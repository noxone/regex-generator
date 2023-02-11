package org.olafneumann.regex.generator

import io.ktor.http.encodeURLParameter
import io.ktor.http.encodeURLPath
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.js.a
import kotlinx.html.js.p
import kotlinx.html.js.pre
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.ui.components.Button
import org.olafneumann.regex.generator.ui.components.createModal
import org.w3c.dom.ErrorEvent
import org.w3c.dom.asList


fun registerErrorHandler() {
    window.addEventListener("error", { event ->
        val error = (event as ErrorEvent).error
        val throwable = if (error is Throwable) error else RegexGeneratorException(error.toString())
        showErrorMessage(throwable)
    })
}

private val gitCommitId: String? get() = document.body?.getAttribute("rg-commit-id")

private val plugins: List<String> get() = window.navigator.plugins.asList().map { it.name }

private fun getErrorReportingText(throwable: Throwable): String = /*"""
**Describe the bug**
""" +*/ """An error occurred with these details:
- Exception: `${throwable.message}`
- Commit ID: `$gitCommitId`
- UserAgent: ${window.navigator.userAgent}
- Vendor: ${window.navigator.vendor}
- Language: ${window.navigator.language}
- Platform: ${window.navigator.platform}
- CPU: ${window.navigator.oscpu}
- TouchPoints: ${window.navigator.maxTouchPoints}
- Plugins:
${plugins.joinToString(separator = "\n") { "  - $it" }}
- StackTrace: ${throwable.stackTraceToString().replace(Regex("\\r\\n"),"\\r\\n")}
""" /*+ """
**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected behavior**
A clear and concise description of what you expected to happen.

**Screenshots**
If applicable, add screenshots to help explain your problem.

**Environment:**
 - Device: [e.g. iPhone6]
 - OS: [e.g. iOS]
 - Browser [e.g. chrome, safari]
 - Version [e.g. 22]

**Additional context**
Add any other context about the problem here.
"""*/

fun showErrorMessage(throwable: Throwable) {
    val textToCopy = getErrorReportingText(throwable)
    createModal(
        title = "Houston, we have a problem!",
        buttons = listOf(
            /*Button(title = "Create issue", colorClass = "warning") {
                window.open(
                    url = "https://github.com/noxone/regex-generator/issues/new" +
                            "?&labels=JavaScript&template=bug_report.md&title=JavaScript-error:&body=" +
                            getErrorReportingText(throwable).encodeURLParameter(true),
                )
            },*/
            Button(title = "Copy text", colorClass = "secondary") { copyToClipboard(textToCopy) },
            Button(title = "Close") { hide() }
        ),
    ) {
        p {
            +"An error occurred in regex generator. This should not happen."
        }
        p {
            +"Please open an "
            a {
                href = "https://github.com/noxone/regex-generator/issues/new" +
                        "?&labels=JavaScript&template=bug_report.md&title=JavaScript-error:&body"+
                        textToCopy.encodeURLParameter(spaceToPlus = true)
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
