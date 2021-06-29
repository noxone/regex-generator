package org.olafneumann.regex.generator.js

import kotlinx.browser.window

external fun encodeURIComponent(input: String): String
external fun decodeURIComponent(input: String): String

fun copyToClipboard(text: String) = window.navigator.clipboard
    .writeText(text)
    .catch(onRejected = { window.alert("Could not copy text: $it") })
