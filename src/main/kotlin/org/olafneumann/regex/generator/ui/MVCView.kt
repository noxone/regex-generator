package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.ui.components.CookieBanner
import org.olafneumann.regex.generator.ui.components.LoadingIndicator
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
    private val loadingIndicator = LoadingIndicator()
    private val cookieBanner = CookieBanner { controller.onDoneAskingUserForCookies(hasGivenConsent = it) }
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

    override fun applyModel(model: DisplayModel) {
        loadingIndicator.applyModel(model)
        cookieBanner.applyModel(model)
        userInputPart.showInputText(model.patternRecognitionModel.input)
        matchPresenterPart.applyModel(model)
        regexDisplay.applyModel(model)
        options.applyModel(model)
        languageDisplay.applyModel(model)
        shareDisplay.applyModel(model)
        footerDisplay.applyModel(model)
    }
}
