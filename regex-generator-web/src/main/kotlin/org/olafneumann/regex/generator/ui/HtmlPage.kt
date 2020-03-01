package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.js.Driver
import org.olafneumann.regex.generator.js.createStepDefinition
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.js.navigator
import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.w3c.dom.HTMLDivElement
import kotlin.dom.addClass
import kotlin.dom.clear


const val CLASS_MATCH_ROW = "rg-match-row"
const val CLASS_MATCH_ITEM = "rg-match-item"
const val CLASS_ITEM_SELECTED = "rg-item-selected"
const val CLASS_ITEM_NOT_AVAILABLE = "rg-item-not-available"

const val EVENT_CLICK = "click"
const val EVENT_INPUT = "input"

const val ID_INPUT_ELEMENT = "rg_raw_input_text"
const val ID_TEXT_DISPLAY = "rg_text_display"
const val ID_RESULT_DISPLAY = "rg_result_display"
const val ID_ROW_CONTAINER = "rg_row_container"
const val ID_CONTAINER_INPUT = "rg_input_container"
const val ID_CHECK_ONLY_MATCHES = "rg_onlymatches"
const val ID_CHECK_CASE_INSENSITIVE = "rg_caseinsensitive"
const val ID_CHECK_DOT_MATCHES_LINE_BRAKES = "rg_dotmatcheslinebreakes"
const val ID_CHECK_MULTILINE = "rg_multiline"
const val ID_BUTTON_COPY = "rg_button_copy"
const val ID_DIV_ONLYMATCH_CONTAINER = "rg_div_onlymatch_container"
const val ID_BUTTON_HELP = "rg_button_show_help"
const val ID_DIV_LANGUAGES = "rg_language_accordion"

private val Int.characterUnits: String get() = "${this}ch"

class HtmlPage(
    private val presenter: DisplayContract.Presenter
) : DisplayContract.View {
    private val RecognizerMatch.row: Int? get() = recognizerMatchToRow[this]
    private val RecognizerMatch.div: HTMLDivElement? get() = recognizerMatchToElements[this]

    private val textInput = HtmlHelper.getInputById(ID_INPUT_ELEMENT)
    private val textDisplay = HtmlHelper.getDivById(ID_TEXT_DISPLAY)
    private val rowContainer = HtmlHelper.getDivById(ID_ROW_CONTAINER)
    private val resultDisplay = HtmlHelper.getDivById(ID_RESULT_DISPLAY)
    private val buttonCopy = HtmlHelper.getButtonById(ID_BUTTON_COPY)
    private val buttonHelp = HtmlHelper.getLinkById(ID_BUTTON_HELP)
    private val checkOnlyMatches = HtmlHelper.getInputById(ID_CHECK_ONLY_MATCHES)
    private val checkCaseInsensitive = HtmlHelper.getInputById(ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getInputById(ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getInputById(ID_CHECK_MULTILINE)
    private val containerLanguages = HtmlHelper.getDivById(ID_DIV_LANGUAGES)

    private val recognizerMatchToRow = mutableMapOf<RecognizerMatch, Int>()
    private val recognizerMatchToElements = mutableMapOf<RecognizerMatch, HTMLDivElement>()

    private val languageDisplays = CodeGenerator.list
        .map {
            it to LanguageCard(it, containerLanguages)
        }
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
    }

    override fun showCopyButton(visible: Boolean) {
        val button = jQuery(buttonCopy)
        if (visible) {
            button.show()
        } else {
            button.hide()
            // val container = jQuery("#$ID_DIV_ONLYMATCH_CONTAINER")
            // container.removeClass()
            // container.addClass("form-group")
        }
    }

    override fun selectInputText() {
        textInput.select()
    }

    override var inputText: String
        get() = textInput.value
        set(value) { textInput.value = value}

    override var displayText: String
        get() = textDisplay.innerText
        set(value) { textDisplay.innerText = value }

    override var resultText: String
        get() = resultDisplay.innerText
        set(value) { resultDisplay.innerText = value }

    override fun showResults(matches: Collection<RecognizerMatch>) {
        // TODO remove CSS class iterator
        var index = 0
        val classes = listOf("primary", "success", "danger", "warning")
        fun getCssClass() = "bg-${classes[index++%classes.size]}"

        fun getElementTitle(match: RecognizerMatch) = "${match.recognizer.name} (${match.inputPart})"

        rowContainer.clear()
        recognizerMatchToRow.clear()
        recognizerMatchToElements.clear()

        // find the correct row for each match
        recognizerMatchToRow.putAll(distributeToRows(matches))
        // Create row elements
        val rowElements = (0..(recognizerMatchToRow.values.max()?:0)).map { createRowElement() }.toList()
        // Create match elements
        matches.forEach { match ->
            val rowElement = rowElements[match.row!!]
            val element = createMatchElement(rowElement)
            recognizerMatchToElements[match] = element
            element.addClass(getCssClass())
            element.style.width = match.inputPart.length.characterUnits
            element.style.left = match.range.first.characterUnits
            element.title = getElementTitle(match)
            element.addEventListener(EVENT_CLICK, { presenter.onSuggestionClick(match)})
        }
    }

    private fun distributeToRows(matches: Collection<RecognizerMatch>): Map<RecognizerMatch, Int> {
        val lines = mutableListOf<Int>()
        return matches.map { match ->
            val indexOfFreeLine = lines.indexOfFirst { it <= match.range.first }
            val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
            lines[line] = match.range.last + 1
            match to line
        }.toMap()
    }


    private fun createRowElement(): HTMLDivElement =
        HtmlHelper.createDivElement(rowContainer, CLASS_MATCH_ROW)
    private fun createMatchElement(parent: HTMLDivElement): HTMLDivElement =
        HtmlHelper.createDivElement(parent, CLASS_MATCH_ITEM)

    override fun select(match: RecognizerMatch, selected: Boolean) {
        match.div?.let { HtmlHelper.toggleClass(it, selected, CLASS_ITEM_SELECTED) }
    }
    override fun disable(match: RecognizerMatch, disabled: Boolean) {
        match.div?.let { HtmlHelper.toggleClass(it, disabled, CLASS_ITEM_NOT_AVAILABLE) }
    }

    override val options: RecognizerCombiner.Options
        get() = RecognizerCombiner.Options(
            onlyPatterns = checkOnlyMatches.checked,
            caseSensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )


    override fun showGeneratedCodeForPattern(pattern: String) {
        val options = options
        CodeGenerator.list
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(pattern, options)) }
        js("Prism.highlightAll();")
    }


    override fun showUserGuide(initialStep: Boolean) {
        driver.reset()
        val steps = arrayOf(createStepDefinition(
                "#rg-title",
                "New to Regex Generator",
                "Hi! It looks like you're new to <em>Regex Generator</em>. Let us show you how to use this tool.",
                "right"
            ),
            createStepDefinition(
                "#$ID_CONTAINER_INPUT",
                "Sample",
                "In the first step we need an example, so please write or paste an example of the text you want to recognize with your regex.",
                "bottom-center"
            ),
            createStepDefinition(
                "#rg_result_container",
                "Recognition",
                "Regex Generator will immediately analyze your text and suggest common patterns you may use.",
                "top-center"
            ),
            createStepDefinition(
                "#$ID_ROW_CONTAINER",
                "Suggestions",
                "Click one or more of suggested patterns...",
                "top"
            ),
            createStepDefinition(
                "#rg_result_display_box",
                "Result",
                "... and we will generate a first <em>regular expression</em> for you. It should be able to match your input text.",
                "top-center"
            ),
            createStepDefinition(
                "#$ID_DIV_LANGUAGES",
                "Language snippets",
                "We will also generate snippets for some languages that show you, how to use the regular expression in your favourite language.",
                "top-left"
            )
        )
        driver.defineSteps(steps)
        driver.start(if (initialStep) 0 else 1)
    }
}


