package org.olafneumann.regex.generator.utils

import kotlinx.browser.window

fun enqueue(delay: Int = 1, action: () -> Unit) {
    window.setTimeout(action, delay)
}
