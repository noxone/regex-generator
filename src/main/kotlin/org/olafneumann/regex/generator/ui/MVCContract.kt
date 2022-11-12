package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.recognizer.RecognizerMatch

interface MVCContract {
    interface View {
        fun applyModel(model: DisplayModel)

        fun showUserGuide(initialStep: Boolean)
    }

    interface Controller {
        fun onFinishedLoading()
        fun onDoneAskingUserForCookies(hasGivenConsent: Boolean)
        fun onUserInputChange(input: String)
        fun onOptionsChange(options: Options)
        fun onRecognizerMatchClick(recognizerMatch: RecognizerMatch)
        fun onCopyRegexButtonClick()
        fun onShareButtonClick()

        fun onUndo()
        fun onRedo()

        fun onShowUserGuide(initialStep: Boolean = false)
    }
}
