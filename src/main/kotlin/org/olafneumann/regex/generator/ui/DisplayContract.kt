package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

interface DisplayContract {
    interface View {
        var inputText: String
        var displayText: String

        var options: RecognizerCombiner.Options

        fun applyInitParameters(defaultText: String)
        fun hideCopyButton()
        fun selectInputText()
        fun showResults(matches: Collection<MatchPresenter>)

        fun showUserGuide(initialStep: Boolean)
        fun setResultingPattern(regex: RecognizerCombiner.RegularExpression)
    }

    interface Controller {
        fun onButtonHelpClick()
        fun onInputTextChanges(newInput: String)
        fun onSuggestionClick(recognizerMatch: RecognizerMatch)
        fun onOptionsChange(options: RecognizerCombiner.Options)
        fun disableUnclickableSuggestions()

        val matchPresenters: Collection<MatchPresenter>
        val allRecognizerMatcher: Collection<RecognizerMatch>
        val selectedRecognizerMatches: Collection<RecognizerMatch>
    }
}
