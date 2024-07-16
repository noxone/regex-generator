package org.olafneumann.regex.generator.js

import kotlinx.browser.window

external fun encodeURIComponent(input: String): String
external fun decodeURIComponent(input: String): String

fun copyToClipboard(text: String, success: () -> Unit = {}) = window.navigator.clipboard
    .writeText(text)
    .then { success() }
    .catch(onRejected = { window.alert("Could not copy text: $it") })
