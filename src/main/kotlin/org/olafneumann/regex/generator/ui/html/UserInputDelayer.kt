package org.olafneumann.regex.generator.ui.html

class UserInputDelayer<T>(
    val immediateAction: (T) -> Unit,
    val delayedAction: (T) -> Unit,
    private val timeout: Int = 200
) {
    private val timerController = TimerController()

    fun onAction(t: T) {
        timerController.setTimeout({ delayedAction(t) }, timeout)

        immediateAction(t)
    }
}
