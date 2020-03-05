package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner

interface DisplayContract {
    interface View {
        var inputText: String
        var displayText: String
        var resultText: String

        val options: RecognizerCombiner.Options

        fun hideCopyButton()
        fun selectInputText()
        fun showResults(matches: Collection<MatchPresenter>)

        fun showUserGuide(initialStep: Boolean)
        fun showGeneratedCodeForPattern(pattern: String)
    }

    interface Presenter {
        fun onButtonCopyClick()
        fun onButtonHelpClick()
        fun onInputChanges(newInput: String)
        fun onSuggestionClick(match: MatchPresenter)
        fun onOptionsChange(options: RecognizerCombiner.Options)
    }
}

