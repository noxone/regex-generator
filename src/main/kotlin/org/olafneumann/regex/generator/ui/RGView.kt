package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.components.CookieBanner
import org.olafneumann.regex.generator.ui.components.LoadingIndicator
import org.olafneumann.regex.generator.ui.components.UserGuide
import org.olafneumann.regex.generator.ui.parts.P1UserInput
import org.olafneumann.regex.generator.ui.parts.P2MatchPresenter
import org.olafneumann.regex.generator.ui.parts.P3RegexDisplay
import org.olafneumann.regex.generator.ui.parts.P4LanguageDisplay
import org.olafneumann.regex.generator.ui.parts.PXFooter
import org.olafneumann.regex.generator.ui.parts.PXOptions
import org.olafneumann.regex.generator.ui.parts.PXShare
import org.olafneumann.regex.generator.ui.parts.PXToolbar
import org.olafneumann.regex.generator.ui.parts.PXUserGuide
import org.olafneumann.regex.generator.ui.parts.PZCapturingGroups
import org.olafneumann.regex.generator.ui.parts.PZOptions

class RGView(
    private val controller: MVCContract.Controller,
    maxInputLength: Int
) : MVCContract.View {
    private val loadingIndicator = LoadingIndicator()
    private val cookieBanner = CookieBanner { controller.onDoneAskingUserForCookies(hasGivenConsent = it) }
    private val toolbar  = PXToolbar(controller = controller)
    private val userInputPart = P1UserInput(
        controller = controller,
        maxInputLength = maxInputLength,
        immediateUserInputAction = { matchPresenterPart.showText(it) }
    )
    private val matchPresenterPart = P2MatchPresenter(controller = controller)
    private val optionsPart = PZOptions()
    //private val capturingGroupPart = PZCapturingGroups()
    private val regexDisplay = P3RegexDisplay(controller = controller)
    private val options = PXOptions(controller = controller)
    private val languageDisplay = P4LanguageDisplay()
    private val shareDisplay = PXShare(controller = controller)
    private val footerDisplay = PXFooter()

    private val userGuide = UserGuide.forLanguage("en")

    init {
        // no reference required
        PXUserGuide(controller = controller)
    }

    override fun applyModel(model: DisplayModel) {
        loadingIndicator.applyModel(model)
        cookieBanner.applyModel(model)
        toolbar.applyModel(model)
        userInputPart.applyModel(model)
        matchPresenterPart.applyModel(model)
        //capturingGroupPart.setRegularExpression(model.patternRecognizerModel.regularExpression)
        regexDisplay.applyModel(model)
        options.applyModel(model)
        languageDisplay.applyModel(model)
        shareDisplay.applyModel(model)
        footerDisplay.applyModel(model)
    }

    override fun showUserGuide(initialStep: Boolean) {
        userGuide.show(initialStep)
    }
}
