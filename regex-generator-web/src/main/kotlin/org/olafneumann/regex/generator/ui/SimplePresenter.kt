package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.navigator
import org.olafneumann.regex.generator.regex.Recognizer
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import kotlin.browser.window


class SimplePresenter : DisplayContract.Presenter {
    private val view: DisplayContract.View = HtmlPage(this)
    private val matches = mutableMapOf<RecognizerMatch, Boolean>()

    val currentTextInput: String get() = view.inputText

    init {
        // if copy is not available: remove copy button
        if (navigator.clipboard == undefined) {
            view.hideCopyButton()
        }
    }

    fun recognizeMatches(input: String = currentTextInput) {
        view.inputText = input
        onInputChanges(input)
        view.selectInputText()
    }

    override fun onButtonCopyClick() {
        navigator.clipboard
            .writeText(view.resultText)
            .catch(onRejected = { window.alert("Could not copy text: $it") })
    }

    override fun onButtonHelpClick() = view.showUserGuide(false)
    fun showInitialUserGuide() = view.showUserGuide(true)

    override fun onInputChanges(newInput: String) {
        matches.clear()
        matches.putAll(
            Recognizer.recognize(newInput)
                .map { it to false }
                .toMap())

        view.displayText = newInput
        view.showResults(matches.keys)
        computeOutputPattern()
    }

    override fun onSuggestionClick(match: RecognizerMatch) {
        if (deactivatedMatches.contains(match)) {
            return
        }
        // determine selected state of the match
        matches[match] = !(matches[match] ?: false)
        // (de)select match in UI
        view.select(match, matches[match] ?: false)
        // find disabled matches
        val disabledMatches = deactivatedMatches
        // disable matches in UI
        matches.keys.forEach { view.disable(it, disabledMatches.contains(it)) }

        computeOutputPattern()
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        val result = RecognizerCombiner.combine(
            view.inputText,
            matches.filter { it.value }.map { it.key }.toList(),
            view.options
        )
        view.resultText = result.pattern
        view.showGeneratedCodeForPattern(result.pattern)
    }

    private val deactivatedMatches: Collection<RecognizerMatch>
        get() {
            val selectedMatches = matches.filter { it.value }.map { it.key }.toList()
            return matches.keys
                .filter { !selectedMatches.contains(it) }
                .filter { match ->
                    selectedMatches.any { selectedMatch ->
                        selectedMatch.intersect(match)
                    }
                }
                .toSet()
        }
}
