package org.olafneumann.regex.generator.ui

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.request.get
import io.ktor.http.Url
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.dom.addClass
import kotlinx.dom.clear
import kotlinx.dom.removeClass
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import kotlinx.html.title
import org.olafneumann.regex.generator.js.Driver
import org.olafneumann.regex.generator.js.JQuery
import org.olafneumann.regex.generator.js.Prism
import org.olafneumann.regex.generator.js.StepDefinition
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.decodeURIComponent
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.UrlGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.url.URL
import kotlin.js.json
import kotlin.math.max

@Suppress("TooManyFunctions")
class HtmlView(
    private val presenter: DisplayContract.Controller
) : DisplayContract.View {
    // extend other classes
    private fun Int.toCharacterUnits() = "${this}ch"

    // HTML elements we need to change
    private val textInput = HtmlHelper.getElementById<HTMLInputElement>(ID_INPUT_ELEMENT)
    private val textDisplay = HtmlHelper.getElementById<HTMLDivElement>(ID_TEXT_DISPLAY)
    private val rowContainer = HtmlHelper.getElementById<HTMLDivElement>(ID_ROW_CONTAINER)
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

    // Stuff needed to display the regex
    private val matchPresenterToRowIndex = mutableMapOf<MatchPresenter, Int>()
    private var inputCharacterSpans = listOf<HTMLSpanElement>()

    private val languageDisplays = CodeGenerator.all
        .associateWith { LanguageCard(it, containerLanguages) }

    private val driver = Driver(js("{}"))

    private var currentPattern = ""

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
        textInput.oninput = { presenter.onInputChanges(inputText) }
        buttonCopy.onclick = { copyToClipboard(currentPattern) }
        buttonShareLink.onclick = { copyToClipboard(textShare.text) }
        buttonHelp.onclick = { presenter.onButtonHelpClick() }
        checkCaseInsensitive.oninput = { presenter.onOptionsChange(options) }
        checkDotAll.oninput = { presenter.onOptionsChange(options) }
        checkMultiline.oninput = { presenter.onOptionsChange(options) }
        checkOnlyMatches.oninput = { presenter.onOptionsChange(options) }
        checkWholeLine.oninput = { presenter.onOptionsChange(options) }
    }

    override fun applyInitParameters() {
        val params = URL(document.URL).searchParams

        val parsedOptions = RecognizerCombiner.Options.parseSearchParams(
            onlyPatternFlag = params.get(SEARCH_ONLY_PATTERNS)?.ifBlank { null },
            matchWholeLineFlag = params.get(SEARCH_MATCH_WHOLE_LINE)?.ifBlank { null },
            regexFlags = params.get(SEARCH_FLAGS)
        )
        this.options = parsedOptions

        params.get(SEARCH_SAMPLE_REGEX)
            ?.ifBlank { null }
            ?.let { inputText = it }

        window.setTimeout({ applyInitSelection() })
    }

    private fun applyInitSelection() {
        val params = URL(document.URL).searchParams

        val selections = params.get(SEARCH_SELECTION)
            ?.ifBlank { null }
            ?.split(",")
            ?.map { it.split("|") }
            ?.associate {
                console.log(it)
                it[0].toInt() to decodeURIComponent(it[1]) }
        if (selections != null) {
            presenter.matchPresenters
                .forEach { presenter ->
                    val selectionName = selections[presenter.ranges[0].first]
                    presenter.recognizerMatches.firstOrNull { it.recognizer.name == selectionName }
                        ?.let { presenter.selectedMatch = it }
                }
            presenter.updatePresentation()
        }
        textShare.updateSearchPattern = true
    }

    override fun hideCopyButton() {
        jQuery(buttonCopy).parent().remove()
    }

    override fun selectInputText() {
        textInput.select()
    }

    override var inputText: String
        get() = textInput.value
        set(value) {
            textInput.value = value
        }

    override var displayText: String
        get() = textDisplay.innerText
        set(value) {
            inputCharacterSpans = value.map { document.create.span(classes = "rg-char") { +it.toString() } }.toList()
            textDisplay.clear()
            inputCharacterSpans.forEach { textDisplay.appendChild(it) }
        }

    override fun showResults(matches: Collection<MatchPresenter>) {
        // TODO remove CSS class iterator
        val indices = mutableMapOf<Int, Int>()
        fun nextCssClass(row: Int): String {
            indices[row] = (indices[row] ?: row) + 1
            return MATCH_PRESENTER_CSS_CLASS[indices[row]!! % MATCH_PRESENTER_CSS_CLASS.size]
        }


        rowContainer.clear()
        matchPresenterToRowIndex.clear()

        // find the correct row for each match
        matchPresenterToRowIndex.putAll(distributeToRows(matches))
        // Create HTML elements
        val rowElements = mutableMapOf<Int, HTMLDivElement>()
        matchPresenterToRowIndex.forEach {  (matchPresenter, rowIndex) ->
            // assign required stuff
            rowElements[rowIndex] = rowElements[rowIndex] ?: createRowElement()
            val rowElement = rowElements[rowIndex]!!
            val cssClass = nextCssClass(rowIndex)

            // create the corresponding match presenter element
            val element = createMatchPresenterElement(matchPresenter)
            rowElement.appendChild(element)
            applyCssStyling(matchPresenter, element, cssClass)
            applyListenersForUserInput(matchPresenter, element, cssClass)
        }

        animateResultDisplaySize(rows = rowElements)
    }

    private fun distributeToRows(matches: Collection<MatchPresenter>): Map<MatchPresenter, Int> {
        val lines = mutableListOf<Int>()
        fun createNextLine(): Int {
            lines.add(0)
            return lines.size - 1
        }
        return matches
            .sortedWith(MatchPresenter.byPriorityAndPosition)
            .associateWith { presenter ->
                val indexOfFreeLine = lines.indexOfFirst { it < presenter.first }
                val line = if (indexOfFreeLine >= 0) indexOfFreeLine else createNextLine()
                lines[line] = presenter.last
                line
            }
    }

    private fun createRowElement(): HTMLDivElement =
        rowContainer.appendChild(document.create.div(classes = CLASS_MATCH_ROW)) as HTMLDivElement

    private fun createMatchPresenterElement(matchPresenter: MatchPresenter): HTMLDivElement =
        document.create.div(classes = CLASS_MATCH_ITEM) {
            div(classes = "rg-match-item-overlay") {
                matchPresenter.recognizerMatches.forEach { match ->
                    div(classes = "rg-recognizer") {
                        a {
                            +match.title
                            onClickFunction = { event ->
                                presenter.onSuggestionClick(match)
                                event.stopPropagation()
                            }
                        }
                    }
                }
            }
            onClickFunction = {
                when {
                    matchPresenter.selected ->
                        matchPresenter.selectedMatch?.let { presenter.onSuggestionClick(it) }
                    matchPresenter.recognizerMatches.size == 1 ->
                        presenter.onSuggestionClick(matchPresenter.recognizerMatches.iterator().next())
                }
            }
        }

    private fun applyCssStyling(matchPresenter: MatchPresenter, element: HTMLDivElement, cssClass: String) {
        element.addClass(cssClass)
        element.style.left = matchPresenter.first.toCharacterUnits()
        element.style.width = matchPresenter.length.toCharacterUnits()
        if (matchPresenter.ranges.size == 2) {
            element.style.borderLeftWidth =
                (matchPresenter.ranges[0].last - matchPresenter.ranges[0].first + 1).toCharacterUnits()
            element.style.borderRightWidth =
                (matchPresenter.ranges[1].last - matchPresenter.ranges[1].first + 1).toCharacterUnits()
        }
        element.classList.toggle(CLASS_ITEM_SELECTED, matchPresenter.selected)
        element.classList.toggle(CLASS_ITEM_NOT_AVAILABLE, matchPresenter.deactivated)
        matchPresenter.onSelectedChanged = { selected ->
            element.classList.toggle(CLASS_ITEM_SELECTED, selected)
            matchPresenter.forEachIndexInRanges { index ->
                inputCharacterSpans[index].classList.toggle(CLASS_CHAR_SELECTED, selected)
            }
        }
        matchPresenter.onDeactivatedChanged =
            { deactivated -> element.classList.toggle(CLASS_ITEM_NOT_AVAILABLE, deactivated) }
    }

    private fun applyListenersForUserInput(matchPresenter: MatchPresenter, element: HTMLDivElement, cssClass: String) {
        element.onmouseenter = {
                if (matchPresenter.availableForHighlight) {
                    matchPresenter.forEachIndexInRanges { index -> inputCharacterSpans[index].addClass(cssClass) }
                }
            }
        element.onmouseleave = {
                if (matchPresenter.availableForHighlight) {
                    matchPresenter.forEachIndexInRanges { index -> inputCharacterSpans[index].removeClass(cssClass) }
                }
            }
    }

    private fun animateResultDisplaySize(rows: Map<Int, HTMLDivElement>) {
        val newHeight = "${computeMatchPresenterAreaHeight(rows)}px"
        val jqRowContainer = jQuery(rowContainer)
        jqRowContainer.stop()
        jqRowContainer.animate(json("height" to newHeight), duration = 350)
    }

    private fun computeMatchPresenterAreaHeight(rows: Map<Int, HTMLDivElement>): Int =
        MAGIC_HEIGHT + (rows.map { computeMatchPresenterBottomLine(it.key, it.value) }
            .maxOrNull() ?: 0)

    private fun computeMatchPresenterBottomLine(rowIndex: Int, element: HTMLDivElement): Int {
        val jqElement = jQuery(element)
        val overlayHeight = getHeight(jqElement.find(".rg-match-item-overlay"))
        val parentHeight = jqElement.height()
        return overlayHeight + parentHeight * (rowIndex + 1)
    }

    private fun getHeight(elements: JQuery): Int {
        val previousCss = elements.attr("style")
        elements.css("position:absolute;visibility:hidden;display:block !important;")
        var maxHeight = 0
        elements.each { jq -> maxHeight = max(maxHeight, jq.height()) }
        elements.attr("style", previousCss ?: "")
        return maxHeight
    }

    override fun setPattern(regex: RecognizerCombiner.RegularExpression) {
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

    private var userGuide: Array<StepDefinition>? = null

    override fun showUserGuide(initialStep: Boolean) {
        if (userGuide == null) {
            GlobalScope.launch {
                loadUserGuide()
                displayUserGuide(initialStep)
            }
        } else {
            displayUserGuide(initialStep)
        }
    }

    private suspend fun loadUserGuide() {
        val client = HttpClient(Js)
        val stepsString = client.get<String>(Url("text/user-guide.en.json"))
        userGuide = JSON.parse<Array<StepDefinition>>(stepsString)
    }

    private fun displayUserGuide(initialStep: Boolean) {
        driver.reset()
        driver.defineSteps(userGuide!!)
        driver.start(if (initialStep) 0 else 1)
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


