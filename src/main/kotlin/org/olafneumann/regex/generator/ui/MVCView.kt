package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.ui.parts.P1UserInput
import org.olafneumann.regex.generator.ui.parts.P2MatchPresenter

class MVCView(
    private val controller: MVCContract.Controller,
    private val maxInputLength: Int
) : MVCContract.View {
    private val userInputPart = P1UserInput(
        controller = controller,
        maxInputLength = maxInputLength,
        immediateUserInputAction = { matchPresenterPart.showText(it) }
    )
    private val matchPresenterPart = P2MatchPresenter(
        controller = controller
    )

    override fun showModel(model: DisplayModel) {
        userInputPart.showInputText(model.patternRecognitionModel.input)
        matchPresenterPart.showText(model.patternRecognitionModel.input)
        matchPresenterPart.showMatchPresenters(model.rowsOfMatchPresenters) { recognizerMatchToCheck ->
            model.patternRecognitionModel.selectedMatches.contains(recognizerMatchToCheck)
        }
    }

    companion object {
        val MATCH_PRESENTER_CSS_CLASS = listOf("bg-primary", "bg-success", "bg-danger", "bg-warning")
    }
}