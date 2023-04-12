package org.olafneumann.regex.generator.js

import kotlinx.browser.window
import org.w3c.dom.Window

@Suppress("NOTHING_TO_INLINE")
private inline fun Window.clarity(action: String): Unit = asDynamic().clarity(action)

@Suppress("NOTHING_TO_INLINE")
private inline fun Window.clarity(action: String, name: String, value: String): Unit =
    asDynamic().clarity(action, name, value)

object Clarity {
    @Suppress("TooGenericExceptionCaught")
    fun consent() {
        try {
            // https://learn.microsoft.com/en-us/clarity/setup-and-installation/cookie-consent
            window.clarity("consent")
        } catch (e: Throwable) {
            console.warn("Unable to activate Clarity consent", e)
        }
    }

    fun set(name: String, value: String) {
        @Suppress("TooGenericExceptionCaught")
        try {
            window.clarity("set", name, value)
        } catch (e: Throwable) {
            console.warn("Unable to report event to Clarity", e)
        }
    }
}
