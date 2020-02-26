package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

interface DisplayContract {
    interface View {
        var inputText: String
        var displayText: String
        var resultText: String

        val options: RecognizerCombiner.Options

        fun showResults(matches: Collection<RecognizerMatch>)
        fun select(match: RecognizerMatch, selected: Boolean)
        fun disable(match: RecognizerMatch, disabled: Boolean)
        fun selectInputText()
    }

    interface Presenter {
        fun onButtonCopyClick()
        fun onInputChanges(newInput: String)
        fun onSuggestionClick(match: RecognizerMatch)
        fun onOptionsChange(options: RecognizerCombiner.Options)
    }
}

