package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

interface DisplayContract {
    interface View {
        // The text entered by the user
        var inputText: String

        var options: RecognizerCombiner.Options

        fun applyInitParameters(defaultText: String)
        fun hideCopyButton()
        fun showUserGuide(initialStep: Boolean)

        fun showShortenWarning()
        fun hideShortenWarning(immediately: Boolean = false)

        fun showMatchingRecognizers(inputText: String, matches: Collection<MatchPresenter>)
        fun showResultingPattern(regex: RecognizerCombiner.RegularExpression)
    }

    interface Controller {
        fun onButtonHelpClick()
        fun onInputTextChanges(newInput: String)
        fun onSuggestionClick(recognizerMatch: RecognizerMatch)
        fun onOptionsChange(options: RecognizerCombiner.Options)
        fun disableNotClickableSuggestions()

        val matchPresenters: Collection<MatchPresenter>
        val allRecognizerMatcher: Collection<RecognizerMatch>
        val selectedRecognizerMatches: Collection<RecognizerMatch>
    }
}
