package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.window

class TimerController {
    private var timerHandle: Int? = null

    fun setTimeout(action: () -> Unit, timeout: Int) {
        timerHandle?.let { window.clearTimeout(it) }
        timerHandle = window.setTimeout(action, timeout)
    }
}
