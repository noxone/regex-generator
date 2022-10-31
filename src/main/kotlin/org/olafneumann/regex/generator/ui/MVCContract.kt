package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

interface MVCContract {
    interface View {
        fun applyModel(model: DisplayModel)
    }

    interface Controller {
        fun onFinishedLoading()
        fun onDoneAskingUserForCookies(hasGivenConsent: Boolean)
        fun onUserInputChange(input: String)
        fun onOptionsChange(options: RecognizerCombiner.Options)
        fun onRecognizerMatchClick(recognizerMatch: RecognizerMatch)
        fun onCopyRegexButtonClick()
        fun onShareButtonClick()
    }
}
