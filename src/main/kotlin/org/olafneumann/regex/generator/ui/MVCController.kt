package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

class MVCController : MVCContract.Controller {
    private val view: MVCContract.View = MVCView(
        controller = this,
        maxInputLength = MAX_INPUT_LENGTH
    )
    private var model: DisplayModel = createInitialModel()
        set(value) {
            field = value
            view.showModel(value)
        }

    init {
        view.showModel(model)
    }

    override fun setUserInput(input: String) {
        model = model.setUserInput(input)
    }

    override fun onClick(recognizerMatch: RecognizerMatch) {
        if (isSelected(recognizerMatch)) {
            deselect(recognizerMatch)
        } else {
            select(recognizerMatch)
        }
    }

    private fun isSelected(recognizerMatch: RecognizerMatch): Boolean =
        model.patternRecognitionModel.selectedMatches.contains(recognizerMatch)

    private fun select(recognizerMatch: RecognizerMatch) {
        model = model.select(recognizerMatch)
    }

    private fun deselect(recognizerMatch: RecognizerMatch) {
        model = model.deselect(recognizerMatch)
    }

    companion object {
        private const val VAL_EXAMPLE_INPUT =
            "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."
        private const val MAX_INPUT_LENGTH = 1000

        private fun createInitialModel() = DisplayModel(
            showLoadingIndicator = true,
            showCookieBanner = true,
            showCopyButton = true,
            patternRecognitionModel = PatternRecognizerModel(
                input = VAL_EXAMPLE_INPUT,
                options = RecognizerCombiner.Options()
            )
        )

    }
}