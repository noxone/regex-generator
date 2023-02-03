package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.components.CookieBanner
import org.olafneumann.regex.generator.ui.components.LoadingIndicator
import org.olafneumann.regex.generator.ui.components.UserGuide
import org.olafneumann.regex.generator.ui.parts.P1UserInput
import org.olafneumann.regex.generator.ui.parts.P2MatchPresenter
import org.olafneumann.regex.generator.ui.parts.P5RegexDisplay
import org.olafneumann.regex.generator.ui.parts.P6LanguageDisplay
import org.olafneumann.regex.generator.ui.parts.PXFooter
import org.olafneumann.regex.generator.ui.parts.PXShare
import org.olafneumann.regex.generator.ui.parts.PXToolbar
import org.olafneumann.regex.generator.ui.parts.PXUserGuide
import org.olafneumann.regex.generator.ui.parts.P3RecognizerCombinerOptions
import org.olafneumann.regex.generator.ui.parts.P4CapturingGroups

class RGView(
    private val controller: MVCContract.Controller,
    maxInputLength: Int
) : MVCContract.View {
    companion object {
        private const val CAP_GROUPS_ENABLED = false

        private const val LANG_EN = "en"
    }

    private val loadingIndicator = LoadingIndicator()
    private val cookieBanner = CookieBanner { controller.onDoneAskingUserForCookies(hasGivenConsent = it) }
    private val toolbar  = PXToolbar(controller = controller)
    private val userInputPart = P1UserInput(
        controller = controller,
        maxInputLength = maxInputLength,
        immediateUserInputAction = { matchPresenterPart.showText(it) }
    )
    private val matchPresenterPart = P2MatchPresenter(controller = controller)
    private val optionsPart = P3RecognizerCombinerOptions(controller = controller)
    private val capturingGroupPart = if (CAP_GROUPS_ENABLED) P4CapturingGroups(controller = controller) else null
    private val regexDisplay = P5RegexDisplay(controller = controller)
    private val languageDisplay = P6LanguageDisplay(controller = controller)
    private val shareDisplay = PXShare(controller = controller)
    private val footerDisplay = PXFooter()

    private val userGuide = UserGuide.forLanguage(LANG_EN)

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
        optionsPart.applyModel(model)
        capturingGroupPart?.applyModel(model)
        regexDisplay.applyModel(model)
        languageDisplay.applyModel(model)
        shareDisplay.applyModel(model)
        footerDisplay.applyModel(model)
    }

    override fun showUserGuide(initialStep: Boolean) {
        userGuide.show(initialStep)
    }
}
