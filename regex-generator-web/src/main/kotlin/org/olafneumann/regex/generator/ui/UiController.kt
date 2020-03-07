package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.navigator
import org.olafneumann.regex.generator.regex.Configuration
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
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

        // handle the cookie banner
        CookieBanner.initialize()

        // Prepare UI
        view.options = ApplicationSettings.viewOptions
    }

    private fun Collection<RecognizerMatch>.toPresentation(): MatchPresenter =
        matches.findLast { it.recognizerMatches.containsAll(this)
                && this.containsAll(it.recognizerMatches) } ?: MatchPresenter(this)

    fun initialize() {
        val initialInput = if (currentTextInput.isBlank()) VAL_EXAMPLE_INPUT else currentTextInput
        recognizeMatches(initialInput)
    }

    private fun recognizeMatches(input: String = currentTextInput) {
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
        val matchGroups = groupMatches(findMatches(newInput))
        matches = matchGroups.map { it.toPresentation() }

        view.displayText = newInput
        view.showResults(matches)
        computeOutputPattern()
    }

    private fun findMatches(input: String): List<RecognizerMatch> =
        Configuration.default.recognizers
            .filter { it.active }
            .flatMap { it.findMatches(input) }
            .sortedWith(HasRange.comparator)

    private fun groupMatches(matches: List<RecognizerMatch>) =
        matches.groupBy { it.ranges }.values

    override fun onSuggestionClick(recognizerMatch: RecognizerMatch) {
        val matchPresenter = matches.find { it.recognizerMatches.contains(recognizerMatch) }
        if (matchPresenter == null || matchPresenter.deactivated) {
            return
        }
        // determine selected state of the presenter
        matchPresenter.selected = !matchPresenter.selected
        // find matches to disable
        matches.filter { !it.selected }
            .forEach { unselected ->
                unselected.deactivated = matches.filter { it.selected }
                    // TODO fix iterator
                    .any { selected ->
                        unselected.recognizerMatches.iterator().next()
                            .intersect(selected.recognizerMatches.iterator().next())
                    }
            }

        computeOutputPattern()
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        ApplicationSettings.viewOptions = options
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        val result = RecognizerCombiner.combine(
            view.inputText,
            // TODO fix iterator
            matches.filter { it.selected }.map { it.recognizerMatches.iterator().next() }.toList(),
            view.options
        )
        view.resultText = result.pattern
        view.showGeneratedCodeForPattern(result.pattern)
    }

    companion object {
        const val VAL_EXAMPLE_INPUT = "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."
    }
}
