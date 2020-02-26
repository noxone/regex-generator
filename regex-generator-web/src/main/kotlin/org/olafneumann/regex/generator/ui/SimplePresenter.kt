package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.copyTextToClipboard
import org.olafneumann.regex.generator.js.Driver
import org.olafneumann.regex.generator.js.createStepDefinition
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch


class SimplePresenter : DisplayContract.Presenter {
    private val view: DisplayContract.View = HtmlPage(this)
    private val matches = mutableMapOf<RecognizerMatch, Boolean>()

    val currentTextInput: String get() = view.inputText

    fun recognizeMatches(input: String = currentTextInput) {
        view.inputText = input
        onInputChanges(input)
        view.selectInputText()
    }

    override fun onButtonCopyClick() {
        copyTextToClipboard(view.resultText)
    }

    override fun onInputChanges(newInput: String) {
        matches.clear()
        matches.putAll(
            RecognizerMatch.recognize(newInput)
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
        matches[match] = !(matches[match]?:false)
        // (de)select match in UI
        view.select(match, matches[match]?:false)
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
    }

    private val deactivatedMatches: Collection<RecognizerMatch> get() {
        val selectedMatches = matches.filter { it.value }.map { it.key }.toList()
        return matches.keys
            .filter { !selectedMatches.contains(it) }
            .filter { match ->
                selectedMatches.any { selectedMatch ->
                    selectedMatch.range.intersect(match.range).isNotEmpty() } }
            .toSet()
    }

    fun showUserGuide() {
        val driver = Driver()
        driver.reset()
        driver.defineSteps(arrayOf(
            createStepDefinition(
                "#rg-title",
                "New to Regex Generator",
                "Hi! It looks like you're new to <em>Regex Generator</em>. Let us show you how to use this tool.",
                "right"
            ),
            createStepDefinition(
                "#$ID_CONTAINER_INPUT",
                "Sample",
                "In the first step we need an example, so please write or paste an example of the text you want to recognize with your regex.",
                "bottom-center"
            ),
            createStepDefinition(
                "#rg_result_container",
                "Recognition",
                "Regex Generator will immediately analyze your text and suggest common patterns you may use.",
                "top-center"
            ),
            createStepDefinition(
                "#$ID_ROW_CONTAINER",
                "Suggestions",
                "Click one or more of suggested patterns...",
                "top"
            ),
            createStepDefinition(
                "#rg_result_display_box",
                "Result",
                "... and we will generate a first <em>regular expression</em> for you. It should be able to match your input text.",
                "top-center")
        ))
        driver.start()
    }
}
