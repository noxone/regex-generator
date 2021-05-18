package org.olafneumann.regex.generator.ui

import kotlinx.browser.window
import org.olafneumann.regex.generator.js.navigator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry


class UiController : DisplayContract.Controller {
    private val view: DisplayContract.View = HtmlView(this)
    private var matches = listOf<MatchPresenter>()

    private val currentTextInput: String get() = view.inputText

    init {
        // if copy is not available: remove copy button
        if (navigator.clipboard == undefined) {
            view.hideCopyButton()
        }

        // handle the cookie banner
        CookieBanner.initialize()

        // Prepare UI
        view.options = ApplicationSettings.viewOptions
    }

    private fun List<RecognizerMatch>.toPresentation(): MatchPresenter =
        matches.findLast {
            it.recognizerMatches.containsAll(this)
                    && this.containsAll(it.recognizerMatches)
        } ?: MatchPresenter(this)

    fun initialize() {
        val initialInput = currentTextInput.ifBlank { VAL_EXAMPLE_INPUT }
        recognizeMatches(initialInput)
    }

    private fun recognizeMatches(input: String = currentTextInput) {
        view.inputText = input
        onInputChanges(input)
        view.selectInputText()
    }

    override fun onButtonCopyClick() {
        // TODO move to view
        navigator.clipboard
            .writeText(view.resultText)
            .catch(onRejected = { window.alert("Could not copy text: $it") })
    }

    override fun onButtonHelpClick() = view.showUserGuide(false)
    fun showInitialUserGuide() = view.showUserGuide(true)

    override fun onInputChanges(newInput: String) {
        val matchGroups = groupMatches(RecognizerRegistry.findMatches(newInput))
        matches = matchGroups.map { it.toPresentation() }

        view.displayText = newInput
        view.showResults(matches)
        computeOutputPattern()
    }

    private fun groupMatches(matches: List<RecognizerMatch>) =
        matches.groupBy { it.ranges }.values

    override fun onSuggestionClick(recognizerMatch: RecognizerMatch) {
        val matchPresenter = matches.find { it.recognizerMatches.contains(recognizerMatch) }
        if (matchPresenter == null || matchPresenter.deactivated) {
            return
        }
        // determine selected state of the presenter
        matchPresenter.selectedMatch = if (matchPresenter.selected) null else recognizerMatch
        // find matches to disable
        val selectedMatches = matches.filter { it.selected }
        matches.filter { !it.selected }
            .forEach { match -> match.deactivated = selectedMatches.any { match.intersect(it) } }

        computeOutputPattern()
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        ApplicationSettings.viewOptions = options
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        val result = RecognizerCombiner.combine(
            view.inputText,
            matches.mapNotNull { it.selectedMatch }.toList(),
            view.options
        )
        view.resultText = result.pattern
        view.showGeneratedCodeForPattern(result.pattern)
    }

    companion object {
        const val VAL_EXAMPLE_INPUT =
            "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."
    }
}
