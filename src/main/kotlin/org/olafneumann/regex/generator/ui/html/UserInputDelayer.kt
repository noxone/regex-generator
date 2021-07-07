package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.window

class UserInputDelayer<T>(
    val immediateAction: (T) -> Unit,
    val delayedAction: (T) -> Unit,
    val timeout: Int = 200
) {
    private var timerHandle: Int? = null

    fun onAction(t: T) {
        timerHandle?.let { window.clearTimeout(it) }
        timerHandle = window.setTimeout({ delayedAction(t) }, timeout)

        immediateAction(t)
    }
}
