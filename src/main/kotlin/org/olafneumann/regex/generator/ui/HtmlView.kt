package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.JQuery
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.ui.html.RecognizerDisplayPart
import org.olafneumann.regex.generator.ui.html.ResultDisplayPart
import org.olafneumann.regex.generator.ui.html.TimerController
import org.olafneumann.regex.generator.ui.html.UserGuide
import org.olafneumann.regex.generator.ui.html.UserInputDelayer
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.InputEvent
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

class HtmlView(
    private val presenter: DisplayContract.Controller,
    private val maxInputLength: Int
) : DisplayContract.View {
    // HTML elements we need to change
    private val textInput = HtmlHelper.getElementById<HTMLInputElement>(ID_INPUT_ELEMENT)
    private val divInputWarning = HtmlHelper.getElementById<HTMLDivElement>(ID_INPUT_MESSAGE_SHORTEN)
    private val buttonHelp = HtmlHelper.getElementById<HTMLAnchorElement>(ID_BUTTON_HELP)
    private val checkOnlyMatches = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_WHOLELINE)
    private val checkCaseInsensitive = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_MULTILINE)

    private val recognizerDisplayPart = RecognizerDisplayPart(presenter)
    private val resultDisplayPart = ResultDisplayPart(this, presenter)
    private val userGuide = UserGuide.forLanguage("en")

    private val inputWarningTimerController = TimerController()

    override var options: RecognizerCombiner.Options
        get() = RecognizerCombiner.Options(
            onlyPatterns = checkOnlyMatches.checked,
            matchWholeLine = checkWholeLine.checked,
            caseInsensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )
        set(value) {
            checkOnlyMatches.checked = value.onlyPatterns
            checkWholeLine.checked = value.matchWholeLine
            checkCaseInsensitive.checked = value.caseInsensitive
            checkDotAll.checked = value.dotMatchesLineBreaks
            checkMultiline.checked = value.multiline
        }

    init {
        val delayer = UserInputDelayer<String>(
            { recognizerDisplayPart.showInputText(it) },
            { presenter.onInputTextChanges(it) }
        )
        textInput.oninput = { handleUserInput { delayer.onAction(it) } }
        buttonHelp.onclick = { presenter.onButtonHelpClick() }
        checkCaseInsensitive.oninput = { presenter.onOptionsChange(options) }
        checkDotAll.oninput = { presenter.onOptionsChange(options) }
        checkMultiline.oninput = { presenter.onOptionsChange(options) }
        checkOnlyMatches.oninput = { presenter.onOptionsChange(options) }
        checkWholeLine.oninput = { presenter.onOptionsChange(options) }

        textInput.maxLength = maxInputLength + 1
        HtmlHelper.getElementById<HTMLSpanElement>(ID_INPUT_MESSAGE_SHORTEN_NUMBER)
            .innerHTML = maxInputLength.toString()
    }

    private fun handleUserInput(action: (String) -> Unit) {
        val rawInputText = inputText
        val actualInputText = if (rawInputText.length <= maxInputLength) {
            rawInputText
        } else {
            showShortenWarning()
            this.inputText.substring(0, maxInputLength)
        }
        inputText = actualInputText
        action(actualInputText)
    }

    override fun applyInitParameters(defaultText: String) {
        val params = URL(document.URL).searchParams

        options = RecognizerCombiner.Options.parseSearchParams(
            onlyPatternFlag = params.get(SEARCH_ONLY_PATTERNS)?.ifBlank { null },
            matchWholeLineFlag = params.get(SEARCH_MATCH_WHOLE_LINE)?.ifBlank { null },
            regexFlags = params.get(SEARCH_FLAGS)
        )

        inputText = params.get(SEARCH_SAMPLE_REGEX)?.ifBlank { null } ?: defaultText
        textInput.select()
        // fire input event to trigger match recognition
        textInput.oninput?.invoke(InputEvent(type = "input"))
        // after event handling (and after match recognition) apply selections
        window.setTimeout({ applyInitSelection(params) })
    }

    private fun applyInitSelection(params: URLSearchParams) {
        val selectionIndexToRecognizerName = params.get(SEARCH_SELECTION)
            ?.ifBlank { null }
            ?.split(",")
            ?.map { it.split("|") }
            ?.associate { it[0].toInt() to decodeURIComponent(it[1]) }
            ?: emptyMap()
        if (selectionIndexToRecognizerName.isNotEmpty()) {
            presenter.matchPresenters
                .forEach { presenter ->
                    val recognizerName = selectionIndexToRecognizerName[presenter.ranges[0].first]
                    presenter.recognizerMatches.firstOrNull { it.recognizer.name == recognizerName }
                        ?.let { presenter.selectedMatch = it }
                }
            presenter.disableNotClickableSuggestions()
        }
    }

    override fun hideCopyButton() = resultDisplayPart.hideCopyButton()

    override var inputText: String
        get() = textInput.value
        set(value) {
            textInput.value = value
        }

    override fun showShortenWarning() {
        jQuery(divInputWarning).slideDown()
        inputWarningTimerController.setTimeout({ hideShortenWarning() }, HIDE_DELAY)
    }

    override fun hideShortenWarning(immediately: Boolean) {
        if (immediately) {
            jQuery(divInputWarning).hide()
        } else {
            jQuery(divInputWarning).slideUp()
        }
    }

    override fun showMatchingRecognizers(inputText: String, matches: Collection<MatchPresenter>) {
        recognizerDisplayPart.showMatchingRecognizers(inputText, matches)
    }

    override fun showResultingPattern(regex: RecognizerCombiner.RegularExpression) {
        resultDisplayPart.showResultingPattern(regex)
    }

    override fun showUserGuide(initialStep: Boolean) {
        userGuide.show(initialStep)
    }

    companion object {
        const val CLASS_MATCH_ROW = "rg-match-row"
        const val CLASS_MATCH_ITEM = "rg-match-item"
        const val CLASS_ITEM_SELECTED = "rg-item-selected"
        const val CLASS_CHAR_SELECTED = "rg-char-selected"
        const val CLASS_ITEM_NOT_AVAILABLE = "rg-item-not-available"

        const val ID_INPUT_ELEMENT = "rg_raw_input_text"
        const val ID_INPUT_MESSAGE_SHORTEN = "rg_raw_input_message_shorten"
        const val ID_INPUT_MESSAGE_SHORTEN_NUMBER = "rg_raw_input_message_shorten_number"
        const val ID_TEXT_DISPLAY = "rg_text_display"
        const val ID_RESULT_DISPLAY = "rg_result_display"
        const val ID_ROW_CONTAINER = "rg_row_container"
        const val ID_CHECK_ONLY_MATCHES = "rg_onlymatches"
        const val ID_CHECK_WHOLELINE = "rg_matchwholeline"
        const val ID_CHECK_CASE_INSENSITIVE = "rg_caseinsensitive"
        const val ID_CHECK_DOT_MATCHES_LINE_BRAKES = "rg_dotmatcheslinebreakes"
        const val ID_CHECK_MULTILINE = "rg_multiline"
        const val ID_BUTTON_COPY = "rg_button_copy"
        const val ID_BUTTON_HELP = "rg_button_show_help"
        const val ID_DIV_LANGUAGES = "rg_language_accordion"
        const val ID_ANCHOR_REGEX101 = "rg_anchor_regex101"
        const val ID_ANCHOR_REGEXR = "rg_anchor_regexr"
        const val ID_BUTTON_SHARE_LINK = "rg_button_copy_share_link"
        const val ID_TEXT_SHARE_LINK = "rg_result_link"

        const val SEARCH_SAMPLE_REGEX = "sampleText"
        const val SEARCH_FLAGS = "flags"
        const val SEARCH_ONLY_PATTERNS = "onlyPatterns"
        const val SEARCH_MATCH_WHOLE_LINE = "matchWholeLine"
        const val SEARCH_SELECTION = "selection"

        val MATCH_PRESENTER_CSS_CLASS = listOf("bg-primary", "bg-success", "bg-danger", "bg-warning")
        const val MAGIC_HEIGHT = 8
        const val HIDE_DELAY = 5000

        fun JQuery.each(function: (JQuery) -> Unit) =
            each { _, htmlElement -> function(jQuery(htmlElement)) }
    }
}


