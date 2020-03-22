package org.olafneumann.regex.generator.ui

import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import org.olafneumann.regex.generator.js.*
import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.UrlGenerator
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLSpanElement
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.clear
import kotlin.dom.removeClass

class HtmlView(
    private val presenter: DisplayContract.Controller
) : DisplayContract.View {
    // extend other classes
    private fun Int.toCharacterUnits() = "${this}ch"

    // HTML elements we need to change
    private val textInput = HtmlHelper.getInputById(ID_INPUT_ELEMENT)
    private val textDisplay = HtmlHelper.getDivById(ID_TEXT_DISPLAY)
    private val rowContainer = HtmlHelper.getDivById(ID_ROW_CONTAINER)
    private val resultDisplay = HtmlHelper.getDivById(ID_RESULT_DISPLAY)
    private val buttonCopy = HtmlHelper.getButtonById(ID_BUTTON_COPY)
    private val buttonHelp = HtmlHelper.getAnchorById(ID_BUTTON_HELP)
    private val checkOnlyMatches = HtmlHelper.getInputById(ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getInputById(ID_CHECK_WHOLELINE)
    private val checkCaseInsensitive = HtmlHelper.getInputById(ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getInputById(ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getInputById(ID_CHECK_MULTILINE)
    private val containerLanguages = HtmlHelper.getDivById(ID_DIV_LANGUAGES)

    private val anchorRegex101 = LinkHandler(
        HtmlHelper.getAnchorById(ID_ANCHOR_REGEX101),
        UrlGenerator("Regex101", "https://regex101.com/?regex=%1\$s&flags=g%2\$s&delimiter=/")
    )
    private val anchorRegexr = LinkHandler(
        HtmlHelper.getAnchorById(ID_ANCHOR_REGEXR),
        UrlGenerator("Regexr", "https://regexr.com/?expression=%1\$s&text=")
    )

    // Stuff needed to display the regex
    private val recognizerMatchToRow = mutableMapOf<MatchPresenter, Int>()
    private var inputCharacterSpans = listOf<HTMLSpanElement>()

    private val languageDisplays = CodeGenerator.all
        .map { it to LanguageCard(it, containerLanguages) }
        .toMap()

    private val driver = Driver(js("{}"))

    init {
        textInput.addEventListener(EVENT_INPUT, { presenter.onInputChanges(inputText) })
        buttonCopy.addEventListener(EVENT_CLICK, { presenter.onButtonCopyClick() })
        buttonHelp.addEventListener(EVENT_CLICK, { presenter.onButtonHelpClick() })
        checkCaseInsensitive.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkDotAll.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkMultiline.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkOnlyMatches.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkWholeLine.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
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

    override var resultText: String
        get() = resultDisplay.innerText
        set(value) {
            resultDisplay.innerText = value
            anchorRegex101.setPattern(value, options)
            anchorRegexr.setPattern(value, options)
        }

    override fun showResults(matches: Collection<MatchPresenter>) {
        // TODO remove CSS class iterator
        val indices = mutableMapOf<Int, Int>()
        val classes = listOf("primary", "success", "danger", "warning")
        fun nextCssClass(row: Int): String {
            indices[row] = (indices[row] ?: row) + 1
            return "bg-${classes[indices[row]!! % classes.size]}"
        }


        rowContainer.clear()
        recognizerMatchToRow.clear()

        // find the correct row for each match
        recognizerMatchToRow.putAll(distributeToRows(matches))
        // Create HTML elements
        val rowElements = mutableMapOf<Int, HTMLDivElement>()
        recognizerMatchToRow.map {
            rowElements[it.value] = rowElements[it.value] ?: createRowElement()
            val rowElement = rowElements[it.value]!!
            val pres = it.key

            // create the corresponding regex element
            val element = document.create.div(classes = CLASS_MATCH_ITEM) {
                div(classes = "rg-match-item-overlay") {
                    pres.recognizerMatches.forEach { match ->
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
                    if (pres.selected) {
                        pres.selectedMatch?.let { presenter.onSuggestionClick(it) }
                    } else if (pres.recognizerMatches.size == 1) {
                        presenter.onSuggestionClick(pres.recognizerMatches.iterator().next())
                    }
                }
            }
            rowElement.appendChild(element)
            // adjust styling
            val cssClass = nextCssClass(it.value)
            element.addClass(cssClass)
            element.style.left = pres.first.toCharacterUnits()
            element.style.width = pres.length.toCharacterUnits()
            if (pres.ranges.size == 2) {
                element.style.borderLeftWidth = (pres.ranges[0].last - pres.ranges[0].first + 1).toCharacterUnits()
                element.style.borderRightWidth = (pres.ranges[1].last - pres.ranges[1].first + 1).toCharacterUnits()
            }
            // add listeners to handle display correctly
            pres.onSelectedChanged = { selected ->
                HtmlHelper.toggleClass(element, selected, CLASS_ITEM_SELECTED)
                pres.forEach { HtmlHelper.toggleClass(inputCharacterSpans[it], selected, CLASS_CHAR_SELECTED) }
            }
            pres.onDeactivatedChanged =
                { deactivated -> HtmlHelper.toggleClass(element, deactivated, CLASS_ITEM_NOT_AVAILABLE) }
            HtmlHelper.toggleClass(element, pres.selected, CLASS_ITEM_SELECTED)
            HtmlHelper.toggleClass(element, pres.deactivated, CLASS_ITEM_NOT_AVAILABLE)
            // add listeners to react on user input
            element.addEventListener(
                EVENT_MOUSE_ENTER,
                {
                    if (pres.availableForHighlight) {
                        pres.forEach { inputCharacterSpans[it].addClass(cssClass) }
                    }
                })
            element.addEventListener(
                EVENT_MOUSE_LEAVE,
                {
                    if (pres.availableForHighlight) {
                        pres.forEach { inputCharacterSpans[it].removeClass(cssClass) }
                    }
                })
        }
    }

    private fun distributeToRows(matches: Collection<MatchPresenter>): Map<MatchPresenter, Int> {
        val lines = mutableListOf<Int>()
        fun createNextLine(): Int {
            lines.add(0)
            return lines.size - 1
        }
        return matches
            .sortedWith(MatchPresenter.byPriorityAndPosition)
            .flatMap { pres -> pres.ranges.map { pres to it } }
            .map { pair ->
                val indexOfFreeLine = lines.indexOfFirst { it < pair.second.first }
                val line = if (indexOfFreeLine >= 0) indexOfFreeLine else createNextLine()
                lines[line] = pair.second.last
                pair.first to line
            }.toMap()
    }


    private fun createRowElement(): HTMLDivElement =
        HtmlHelper.createDivElement(rowContainer, CLASS_MATCH_ROW)

    override var options: RecognizerCombiner.Options
        get() = RecognizerCombiner.Options(
            onlyPatterns = checkOnlyMatches.checked,
            matchWholeLine = checkWholeLine.checked,
            caseSensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )
        set(value) {
            checkOnlyMatches.checked = value.onlyPatterns
            checkWholeLine.checked = value.matchWholeLine
            checkCaseInsensitive.checked = value.caseSensitive
            checkDotAll.checked = value.dotMatchesLineBreaks
            checkMultiline.checked = value.multiline
        }


    override fun showGeneratedCodeForPattern(pattern: String) {
        val options = options
        CodeGenerator.all
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(pattern, options)) }
        Prism.highlightAll()
    }


    override fun showUserGuide(initialStep: Boolean) {
        driver.reset()
        driver.defineSteps(
            listOf(
                StepDefinition(
                    "#rg-title", Popover(
                        "New to Regex Generator",
                        "Hi! It looks like you're new to <em>Regex Generator</em>. Let us show you how to use this tool.",
                        "right"
                    )
                ),
                StepDefinition(
                    "#$ID_CONTAINER_INPUT", Popover(
                        "Sample",
                        "In the first step we need an example, so please write or paste an example of the text you want to recognize with your regex.",
                        "bottom-center"
                    )
                ),
                StepDefinition(
                    "#rg_result_container", Popover(
                        "Recognition",
                        "Regex Generator will immediately analyze your text and suggest common patterns you may use.",
                        "top-center"
                    )
                ),
                StepDefinition(
                    "#$ID_ROW_CONTAINER", Popover(
                        "Suggestions",
                        "Click one or more of suggested patterns...",
                        "top"
                    )
                ),
                StepDefinition(
                    "#rg_result_display_box", Popover(
                        "Result",
                        "... and we will generate a first <em>regular expression</em> for you. It should be able to match your input text.",
                        "top-center"
                    )
                ),
                StepDefinition(
                    "#$ID_DIV_LANGUAGES", Popover(
                        "Language snippets",
                        "We will also generate snippets for some languages that show you, how to use the regular expression in your favourite language.",
                        "top-left"
                    )
                )
            )
        )
        driver.start(if (initialStep) 0 else 1)
    }

    companion object {
        const val CLASS_MATCH_ROW = "rg-match-row"
        const val CLASS_MATCH_ITEM = "rg-match-item"
        const val CLASS_ITEM_SELECTED = "rg-item-selected"
        const val CLASS_CHAR_SELECTED = "rg-char-selected"
        const val CLASS_ITEM_NOT_AVAILABLE = "rg-item-not-available"

        const val EVENT_CLICK = "click"
        const val EVENT_INPUT = "input"
        const val EVENT_MOUSE_ENTER = "mouseenter"
        const val EVENT_MOUSE_LEAVE = "mouseleave"

        const val ID_INPUT_ELEMENT = "rg_raw_input_text"
        const val ID_TEXT_DISPLAY = "rg_text_display"
        const val ID_RESULT_DISPLAY = "rg_result_display"
        const val ID_ROW_CONTAINER = "rg_row_container"
        const val ID_CONTAINER_INPUT = "rg_input_container"
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
    }
}


