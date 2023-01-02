package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions
import org.olafneumann.regex.generator.settings.ApplicationSettings
import org.olafneumann.regex.generator.ui.RGController.Companion.applyInitialSelection
import org.olafneumann.regex.generator.ui.model.FlagHelper
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

@Suppress("TooManyFunctions")
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

    override fun onCodeGeneratorOptionsChange(options: CodeGeneratorOptions) {
        model = model.setCodeGeneratorOptions(options)
    }

    override fun onRecognizerCombinerOptionsChange(options: RecognizerMatchCombinerOptions) {
        model = model.setRecognizerMatchCombinerOptions(options)
    }

    override fun onRecognizerMatchClick(recognizerMatch: RecognizerMatch) {
        if (isSelected(recognizerMatch)) {
            deselect(recognizerMatch)
        } else {
            select(recognizerMatch)
        }
    }

    override fun onCopyRegexButtonClick() {
        copyToClipboard(text = model.patternRecognizerModel.finalPattern)
    }

    override fun onShareButtonClick(success: () -> Unit) {
        copyToClipboard(text = model.shareLink.toString(), success = success)
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

    override fun onNewCapturingGroupModel(capturingGroupModel: CapturingGroupModel) {
        model = model.setCapturingGroupModel(capturingGroupModel)
    }

    companion object {
        private const val VAL_EXAMPLE_INPUT =
            "2020-03-12T13:34:56.123Z INFO  [org.example.Class]: This is a #simple #logline containing a 'value'."
        private const val MAX_INPUT_LENGTH = 1000

        private fun createInitialModel(): DisplayModel {
            val params = URL(document.URL).searchParams

            val regexFlags = params.get(HtmlView.SEARCH_FLAGS)
            val codeGeneratorOptions = FlagHelper.parseCodeGeneratorOptions(regexFlags)
            val recognizerMatchCombinerOptions = FlagHelper.parseRecognizerMatchCombinerOptions(regexFlags)
            val inputText = params.get(HtmlView.SEARCH_SAMPLE_REGEX)?.ifBlank { null } ?: VAL_EXAMPLE_INPUT

            val patternRecognizerModel = PatternRecognizerModel(
                input = inputText,
                recognizerMatchCombinerOptions = recognizerMatchCombinerOptions
            )

            return DisplayModel(
                showLoadingIndicator = true,
                showCookieBanner = !isUserConsentGiven,
                showCopyButton = isClipboardAvailable,
                codeGeneratorOptions = codeGeneratorOptions,
                patternRecognizerModels = listOf(
                    patternRecognizerModel
                        .applyInitialSelection(params)
                        .applyInitialCapturingGroups(params)
                ),
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
                .mapNotNull { (index, name) ->
                    recognizerMatches
                        .firstOrNull { it.first == index && it.recognizer.name == name }
                }
                .forEach { outModel = outModel.select(it) }
            return outModel
        }

        private fun PatternRecognizerModel.applyInitialCapturingGroups(
            params: URLSearchParams
        ): PatternRecognizerModel {
            val rangeToCapturingGroupName: Map<IntRange, String?>
            try {
                rangeToCapturingGroupName = params.get(HtmlView.SEARCH_CAP_GROUP)
                    ?.ifBlank { null }
                    ?.split(",")
                    ?.asSequence()
                    ?.map { it.split("|") }
                    ?.filter { it.size == 2 }
                    ?.map { it[1].toIntRange() to it[0].ifEmpty { null } }
                    ?.filter { it.first != null }
                    ?.associate { it.first!! to it.second }
                    ?: emptyMap()
            } catch (e: IllegalArgumentException) {
                console.warn("Unable to read state from URL", e)
                return this
            }

            var outModel = this
            rangeToCapturingGroupName
                .entries
                .sortedBy { it.key.first }
                .forEach { (range, name) ->
                    outModel = outModel.setCapturingGroupModel(
                        outModel.capturingGroupModel.addCapturingGroup(
                            start = range.first,
                            endInclusive = range.last,
                            name = name
                        )
                    )
                }
            return outModel
        }

        private fun String.toIntRange(): IntRange? {
            val parts = this.split("-")
            if (parts.size != 2) return null
            return IntRange(start = parts[0].tryParsingToInt(), endInclusive = parts[1].tryParsingToInt())
        }

        private fun String.tryParsingToInt(): Int {
            return try {
                this.toInt()
            } catch (e: IllegalArgumentException) {
                console.warn("Unable to read state from URL", e)
                -1
            }
        }

        private val isClipboardAvailable: Boolean
            get() =
                window.navigator.clipboard != undefined

        private val isUserConsentGiven: Boolean
            get() =
                ApplicationSettings.hasUserConsent
    }
}
