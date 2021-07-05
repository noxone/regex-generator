package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.clear
import kotlinx.html.dom.create
import kotlinx.html.js.span
import kotlinx.html.title
import org.olafneumann.regex.generator.js.JQuery
import org.olafneumann.regex.generator.js.Prism
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.UrlGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.ui.html.RecognizerDisplayPart
import org.olafneumann.regex.generator.ui.html.UserGuide
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.InputEvent
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

class HtmlView(
    private val presenter: DisplayContract.Controller
) : DisplayContract.View {
    // HTML elements we need to change
    private val textInput = HtmlHelper.getElementById<HTMLInputElement>(ID_INPUT_ELEMENT)
    private val resultDisplay = HtmlHelper.getElementById<HTMLDivElement>(ID_RESULT_DISPLAY)
    private val buttonCopy = HtmlHelper.getElementById<HTMLButtonElement>(ID_BUTTON_COPY)
    private val buttonHelp = HtmlHelper.getElementById<HTMLAnchorElement>(ID_BUTTON_HELP)
    private val buttonShareLink = HtmlHelper.getElementById<HTMLButtonElement>(ID_BUTTON_SHARE_LINK)
    private val checkOnlyMatches = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_WHOLELINE)
    private val checkCaseInsensitive = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getElementById<HTMLInputElement>(ID_CHECK_MULTILINE)
    private val containerLanguages = HtmlHelper.getElementById<HTMLDivElement>(ID_DIV_LANGUAGES)

    private val anchorRegex101 = LinkHandler(
        HtmlHelper.getElementById(ID_ANCHOR_REGEX101),
        UrlGenerator("Regex101", "https://regex101.com/?regex=%1\$s&flags=g%2\$s&delimiter=/")
    )
    private val anchorRegexr = LinkHandler(
        HtmlHelper.getElementById(ID_ANCHOR_REGEXR),
        UrlGenerator("Regexr", "https://regexr.com/?expression=%1\$s&text=")
    )
    private val textShare = TextHandler(
        HtmlHelper.getElementById(ID_TEXT_SHARE_LINK),
        UrlGenerator("ShareLink", "https://regex-generator.olafneumann.org/?sampleText=%1\$s&flags=%2\$s")
    )

    private val languageDisplays = CodeGenerator.all
        .associateWith { LanguageCard(it, containerLanguages) }

    private var currentPattern = ""

    private val recognizerDisplayPart = RecognizerDisplayPart(presenter)
    private val userGuide = UserGuide.forLanguage("en")

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
        textInput.oninput = { presenter.onInputTextChanges(inputText) }
        buttonCopy.onclick = { copyToClipboard(currentPattern) }
        buttonShareLink.onclick = { copyToClipboard(textShare.text) }
        buttonHelp.onclick = { presenter.onButtonHelpClick() }
        checkCaseInsensitive.oninput = { presenter.onOptionsChange(options) }
        checkDotAll.oninput = { presenter.onOptionsChange(options) }
        checkMultiline.oninput = { presenter.onOptionsChange(options) }
        checkOnlyMatches.oninput = { presenter.onOptionsChange(options) }
        checkWholeLine.oninput = { presenter.onOptionsChange(options) }
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
            ?.associate {
                console.log(it)
                it[0].toInt() to decodeURIComponent(it[1])
            }
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

    override fun hideCopyButton() {
        jQuery(buttonCopy).parent().remove()
    }

    override var inputText: String
        get() = textInput.value
        set(value) {
            textInput.value = value
        }

    override fun showMatchingRecognizers(inputText: String, matches: Collection<MatchPresenter>) {
        recognizerDisplayPart.showMatchingRecognizers(inputText, matches)
    }

    override fun showResultingPattern(regex: RecognizerCombiner.RegularExpression) {
        showResultRegex(regex)

        // update share-link
        textShare.setPattern(inputText, options, presenter.selectedRecognizerMatches)

        // update links
        currentPattern = regex.pattern
        anchorRegex101.setPattern(currentPattern, options)
        anchorRegexr.setPattern(currentPattern, options)

        // update programming languages
        val options = options
        CodeGenerator.all
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(currentPattern, options)) }
        Prism.highlightAll()
    }

    private fun showResultRegex(regex: RecognizerCombiner.RegularExpression) {
        resultDisplay.clear()
        for (part in regex.parts) {
            resultDisplay.append(
                document.create.span(classes = "rg-result-part") {
                    title = when {
                        part.match != null -> "Recognizes \"${part.match.title}\" using the highlighted regex"
                        part.title != null -> "Recognizes \"${part.title}\""
                        part.originalText != null -> "Recognizes exactly \"${part.originalText}\""
                        else -> ""
                    }
                    +part.pattern
                }
            )
        }
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

        fun JQuery.each(function: (JQuery) -> Unit) =
            each { _, htmlElement -> function(jQuery(htmlElement)) }
    }
}


