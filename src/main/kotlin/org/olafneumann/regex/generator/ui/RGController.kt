package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.settings.ApplicationSettings
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

    override fun onRedo() {
        model = model.performRedo()
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

    override fun onOptionsChange(options: Options) {
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
        copyToClipboard(text = model.patternRecognizerModel.regularExpression.finalPattern)
    }

    override fun onShareButtonClick() {
        copyToClipboard(text = model.shareLink.toString())
    }

    private fun isSelected(recognizerMatch: RecognizerMatch): Boolean =
        model.patternRecognizerModel.selectedRecognizerMatches.contains(recognizerMatch)

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

            val options = Options.parseSearchParams(
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
                patternRecognizerModels = listOf(patternRecognizerModel.applyInitialSelection(params)),
                modelPointer = 0
            )
        }

        private fun PatternRecognizerModel.applyInitialSelection(
            params: URLSearchParams
        ): PatternRecognizerModel {
            val selectionIndexToRecognizerName: Map<Int, String>
            try {
                selectionIndexToRecognizerName = params.get(HtmlView.SEARCH_SELECTION)
                    ?.ifBlank { null }
                    ?.split(",")
                    ?.map { it.split("|") }
                    ?.filter { it.size == 2 }
                    ?.associate { it[0].tryParsingToInt() to decodeURIComponent(it[1]) }
                    ?.filter { it.key >= 0 }
                    ?: emptyMap()
            } catch (e: IllegalArgumentException) {
                console.warn("Unable to read state from URL", e)
                return this
            }

            var outModel = this
            selectionIndexToRecognizerName
                .mapNotNull { (index, name) -> recognizerMatches
                    .firstOrNull { it.first == index && it.recognizer.name == name }
                }
                .forEach { outModel = outModel.select(it) }
            return outModel
        }

        private fun String.tryParsingToInt(): Int {
            return try {
                this.toInt()
            } catch (e: IllegalArgumentException) {
                console.warn("Unable to read state from URL", e)
                -1
            }
        }

        private val isClipboardAvailable: Boolean get() =
            window.navigator.clipboard != undefined

        private val isUserConsentGiven: Boolean get() =
            ApplicationSettings.hasUserConsent
    }
}
