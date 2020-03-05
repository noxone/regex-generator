package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.navigator
import org.olafneumann.regex.generator.regex.Configuration
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import kotlin.browser.window


class UiController : DisplayContract.Controller {
    private val view: DisplayContract.View = HtmlView(this)
    private var matches = listOf<MatchPresenter>()

    val currentTextInput: String get() = view.inputText

    init {
        // if copy is not available: remove copy button
        if (navigator.clipboard == undefined) {
            view.hideCopyButton()
        }
    }

    private fun RecognizerMatch.toPresentation() =
        matches.find { it.recognizerMatches[0] == this } ?: MatchPresenter(listOf(this))

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
        matches = findMatches(newInput).map { it.toPresentation() }

        view.displayText = newInput
        view.showResults(matches)
        computeOutputPattern()
    }

    private fun findMatches(input: String): List<RecognizerMatch> =
        Configuration.default.recognizers
            .filter { it.active }
            .flatMap { it.findMatches(input) }

    override fun onSuggestionClick(match: MatchPresenter) {
        if (match.deactivated) {
            return
        }
        // determine selected state of the match
        match.selected = !match.selected
        // find matches to disable
        matches.filter { !it.selected }
            .forEach { unselected ->
                unselected.deactivated = matches.filter { it.selected }
                .any { selected -> unselected.recognizerMatches[0].intersect(selected.recognizerMatches[0]) } }

        computeOutputPattern()
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        val result = RecognizerCombiner.combine(
            view.inputText,
            matches.filter { it.selected }.map { it.recognizerMatches[0] }.toList(),
            view.options
        )
        view.resultText = result.pattern
        view.showGeneratedCodeForPattern(result.pattern)
    }
}
