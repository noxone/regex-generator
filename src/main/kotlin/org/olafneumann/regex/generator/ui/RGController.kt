package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.settings.ApplicationSettings
import org.olafneumann.regex.generator.ui.HtmlView.toCurrentWindowLocation
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

class RGController : MVCContract.Controller {
    private val view: MVCContract.View = RGView(
        controller = this,
        maxInputLength = MAX_INPUT_LENGTH
    )

    private var model: DisplayModel = createInitialModel()
        set(value) {
            field = value
            view.applyModel(value)
        }

    init {
        view.applyModel(model)
    }

    override fun onUndo() {
        model = model.performUndo()
    }

    override fun onFinishedLoading() {
        model = model.setLoadingIndicatorVisible(false)
    }

    override fun onDoneAskingUserForCookies(hasGivenConsent: Boolean) {
        ApplicationSettings.hasUserConsent = hasGivenConsent
        model = model.setCookieBannerVisible(false)
    }

    override fun onUserInputChange(input: String) {
        model = model.setUserInput(input)
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        model = model.setOptions(options)
    }

    override fun onRecognizerMatchClick(recognizerMatch: RecognizerMatch) {
        if (isSelected(recognizerMatch)) {
            deselect(recognizerMatch)
        } else {
            select(recognizerMatch)
        }
    }

    override fun onCopyRegexButtonClick() {
        copyToClipboard(text = model.patternRecognitionModel.regularExpression.pattern)
    }

    override fun onShareButtonClick() {
        copyToClipboard(text = model.shareLink.toCurrentWindowLocation().toString())
    }

    private fun isSelected(recognizerMatch: RecognizerMatch): Boolean =
        model.patternRecognitionModel.selectedRecognizerMatches.contains(recognizerMatch)

    private fun select(recognizerMatch: RecognizerMatch) {
        model = model.select(recognizerMatch)
    }

    private fun deselect(recognizerMatch: RecognizerMatch) {
        model = model.deselect(recognizerMatch)
    }

    override fun onShowUserGuide(initialStep: Boolean) {
        view.showUserGuide(initialStep)
    }

    companion object {
        private const val VAL_EXAMPLE_INPUT =
            "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."
        private const val MAX_INPUT_LENGTH = 1000

        private fun createInitialModel(): DisplayModel {
            val params = URL(document.URL).searchParams

            val options = RecognizerCombiner.Options.parseSearchParams(
                onlyPatternFlag = params.get(HtmlView.SEARCH_ONLY_PATTERNS)?.ifBlank { null },
                matchWholeLineFlag = params.get(HtmlView.SEARCH_MATCH_WHOLE_LINE)?.ifBlank { null },
                regexFlags = params.get(HtmlView.SEARCH_FLAGS)
            )
            val inputText = params.get(HtmlView.SEARCH_SAMPLE_REGEX)?.ifBlank { null } ?: VAL_EXAMPLE_INPUT

            val patternRecognizerModel = PatternRecognizerModel(
                input = inputText,
                options = options
            )

            return DisplayModel(
                showLoadingIndicator = true,
                showCookieBanner = !isUserConsentGiven,
                showCopyButton = isClipboardAvailable,
                patternRecognitionModel = applyInitialSelection(patternRecognizerModel, params),
                oldPatternRecognizerModels = emptyList()
            )
        }

        private fun applyInitialSelection(
            model: PatternRecognizerModel,
            params: URLSearchParams
        ): PatternRecognizerModel {
            var outModel = model
            val selectionIndexToRecognizerName = params.get(HtmlView.SEARCH_SELECTION)
                ?.ifBlank { null }
                ?.split(",")
                ?.map { it.split("|") }
                ?.associate { it[0].toInt() to decodeURIComponent(it[1]) }
                ?: emptyMap()
            selectionIndexToRecognizerName.entries.forEach { (index, name) ->
                model.recognizerMatches
                    .firstOrNull { it.first == index && it.recognizer.name == name }
                    ?.let { outModel = outModel.select(it) }
            }
            return outModel
        }

        private val isClipboardAvailable: Boolean get() =
            window.navigator.clipboard != undefined

        private val isUserConsentGiven: Boolean get() =
            ApplicationSettings.hasUserConsent
    }
}
