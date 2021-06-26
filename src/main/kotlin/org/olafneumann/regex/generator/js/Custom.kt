package org.olafneumann.regex.generator.js

import kotlinx.browser.window
import kotlin.js.Promise

external val navigator: Navigator

open external class Navigator {
    val clipboard: Clipboard
}
external class Clipboard {
    fun writeText(text: String): Promise<Any>
}

external fun encodeURIComponent(input: String): String

fun copyToClipboard(text: String) = navigator.clipboard
    .writeText(text)
    .catch(onRejected = { window.alert("Could not copy text: $it") })