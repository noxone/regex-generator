package org.olafneumann.regex.generator.js

import kotlinx.browser.window
import org.w3c.dom.Window

@Suppress("NOTHING_TO_INLINE")
private inline fun Window.clarity(action: String): Unit = asDynamic().clarity(action)

object Clarity {
    fun consent() {
        try {
            // https://learn.microsoft.com/en-us/clarity/setup-and-installation/cookie-consent
            window.clarity("consent")
        } catch (e: Throwable) {
            console.warn("Unable to activate Clarity consent", e)
        }
    }
}
