package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.ui.parts.P1UserInput
import org.olafneumann.regex.generator.ui.parts.P2MatchPresenter
import org.olafneumann.regex.generator.ui.parts.P3RegexDisplay
import org.olafneumann.regex.generator.ui.parts.P4LanguageDisplay
import org.olafneumann.regex.generator.ui.parts.PXFooter
import org.olafneumann.regex.generator.ui.parts.PXOptions
import org.olafneumann.regex.generator.ui.parts.PXShare

class MVCView(
    private val controller: MVCContract.Controller,
    private val maxInputLength: Int
) : MVCContract.View {
    private val userInputPart = P1UserInput(
        controller = controller,
        maxInputLength = maxInputLength,
        immediateUserInputAction = { matchPresenterPart.showText(it) }
    )
    private val matchPresenterPart = P2MatchPresenter(controller = controller)
    private val regexDisplay = P3RegexDisplay(controller = controller)
    private val options = PXOptions(controller = controller)
    private val languageDisplay = P4LanguageDisplay()
    private val shareDisplay = PXShare(controller = controller)
    private val footerDisplay = PXFooter()

    override fun showModel(model: DisplayModel) {
        userInputPart.showInputText(model.patternRecognitionModel.input)
        matchPresenterPart.showText(model.patternRecognitionModel.input)
        matchPresenterPart.showMatchPresenters(model.rowsOfMatchPresenters) { recognizerMatchToCheck ->
            model.patternRecognitionModel.selectedRecognizerMatches.contains(recognizerMatchToCheck)
        }
        regexDisplay.applyModel(model)
        options.applyModel(model)
        languageDisplay.applyModel(model)
        shareDisplay.applyModel(model)
        footerDisplay.applyModel(model)
    }

    companion object {
        val MATCH_PRESENTER_CSS_CLASS = listOf("bg-primary", "bg-success", "bg-danger", "bg-warning")
    }
}