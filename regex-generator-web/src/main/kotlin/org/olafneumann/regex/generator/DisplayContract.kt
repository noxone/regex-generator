package org.olafneumann.regex.generator

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.clear
import kotlin.dom.hasClass
import kotlin.dom.removeClass

const val CLASS_HIDDEN = "rg-hidden"
const val CLASS_MATCH_ROW = "rg-match-row"
const val CLASS_MATCH_ITEM = "rg-match-item"
const val CLASS_ITEM_SELECTED = "rg-item-selected"
const val CLASS_ITEM_NOT_AVAILABLE = "rg-item-not-available"

const val ELEMENT_DIV = "div"

const val EVENT_CLICK = "click"
const val EVENT_INPUT = "input"

const val ID_INPUT_ELEMENT = "rg_raw_input_text"
const val ID_TEXT_DISPLAY = "rg_text_display"
const val ID_RESULT_DISPLAY = "rg_result_display"
const val ID_ROW_CONTAINER = "rg_row_container"
const val ID_CONTAINER_INPUT = "rg_input_container"
const val ID_CONTAINER_PATTERN_SELECTION = "rg_pattern_selection_container"
const val ID_CONTAINER_RESULT = "rg_regex_result_container"
const val ID_CHECK_ONLY_MATCHES = "rg_onlymatches"
const val ID_CHECK_CASE_INSENSITIVE = "rg_caseinsensitive"
const val ID_CHECK_DOT_MATCHES_LINE_BRAKES = "rg_dotmatcheslinebreakes"
const val ID_CHECK_MULTILINE = "rg_dotmatcheslinebreakes"

private val Int.characterUnits: String get() = "${this}ch"

interface DisplayContract {
    interface View {
        fun getTextInput(): String
        fun showText(text: String)
        fun showResults(matches: Collection<RecognizerMatch>)
        fun select(match: RecognizerMatch, selected: Boolean)
        fun disable(match: RecognizerMatch, disabled: Boolean)

        var inputContainerVisible: Boolean
        var patternSelectionContainerVisible: Boolean
        var resultVisible: Boolean
        var resultText: String

        val options: RecognizerCombiner.Options
    }

    interface Presenter {
        fun onInputChanges(newInput: String)
        fun onSuggestionClick(match: RecognizerMatch)
        fun onOptionsChange(options: RecognizerCombiner.Options)
    }
}

class DisplayPage(
    private val presenter: DisplayContract.Presenter
) : DisplayContract.View {
    private val RecognizerMatch.row: Int? get() = recognizerMatchToRow[this]
    private val RecognizerMatch.div: HTMLDivElement? get() = recognizerMatchToElements[this]

    private val textInput = getInputById(ID_INPUT_ELEMENT)
    private val textDisplay = getDivById(ID_TEXT_DISPLAY)
    private val rowContainer = getDivById(ID_ROW_CONTAINER)
    private val inputContainer = getDivById(ID_CONTAINER_INPUT)
    private val patternSelectionContainer = getDivById(ID_CONTAINER_PATTERN_SELECTION)
    private val resultContainer = getDivById(ID_CONTAINER_RESULT)
    private val resultDisplay = getDivById(ID_RESULT_DISPLAY)
    private val checkOnlyMatches = getInputById(ID_CHECK_ONLY_MATCHES)
    private val checkCaseInsensitive = getInputById(ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = getInputById(ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = getInputById(ID_CHECK_MULTILINE)

    private val recognizerMatchToRow = mutableMapOf<RecognizerMatch, Int>()
    private val recognizerMatchToElements = mutableMapOf<RecognizerMatch, HTMLDivElement>()

    init {
        textInput.addEventListener(EVENT_INPUT, { presenter.onInputChanges(getTextInput()) })
        checkCaseInsensitive.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkDotAll.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkMultiline.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
        checkOnlyMatches.addEventListener(EVENT_INPUT, { presenter.onOptionsChange(options) })
    }

    private fun getDivById(id: String): HTMLDivElement {
        try {
            return document.getElementById(id) as HTMLDivElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find div with id '$id'.", e)
        }
    }

    private fun getInputById(id: String): HTMLInputElement {
        try {
            return document.getElementById(id) as HTMLInputElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find input with id '$id'.", e)
        }
    }

    override fun getTextInput() = textInput.value

    override fun showText(text: String) {
        textDisplay.innerText = text
    }

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
            val indexOfFreeLine = lines.indexOfFirst { it < match.range.first }
            val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
            lines[line] = match.range.last + 1
            match to line
        }.toMap()
    }

    private fun createRowElement(): HTMLDivElement = createDivElement(rowContainer, CLASS_MATCH_ROW)

    private fun createMatchElement(parent: HTMLDivElement): HTMLDivElement = createDivElement(parent, CLASS_MATCH_ITEM)

    private fun createDivElement(parent: Node, vararg classNames: String): HTMLDivElement {
        val element = document.createElement(ELEMENT_DIV) as HTMLDivElement
        element.addClass(*classNames)
        parent.appendChild(element)
        return element
    }

    override var inputContainerVisible: Boolean
        get() = inputContainer.hasClass(CLASS_HIDDEN)
        set(value) { toggleClass(inputContainer, !value, CLASS_HIDDEN) }
    override var patternSelectionContainerVisible: Boolean
        get() = patternSelectionContainer.hasClass(CLASS_HIDDEN)
        set(value) { toggleClass(patternSelectionContainer, !value, CLASS_HIDDEN) }
    override var resultVisible: Boolean
        get() = resultContainer.hasClass(CLASS_HIDDEN)
        set(value) { toggleClass(resultContainer, !value, CLASS_HIDDEN) }
    override fun select(match: RecognizerMatch, selected: Boolean) = match.div?.let { toggleClass(it, selected, CLASS_ITEM_SELECTED) }!!
    override fun disable(match: RecognizerMatch, disabled: Boolean) = match.div?.let { toggleClass(it, disabled, CLASS_ITEM_NOT_AVAILABLE) }!!

    private fun toggleClass(element: HTMLDivElement, selected: Boolean, className: String) {
        if (selected)
            element.addClass(className)
        else
            element.removeClass(className)
    }

    override val options: RecognizerCombiner.Options
        get() = RecognizerCombiner.Options(
            onlyPatterns = checkOnlyMatches.checked,
            caseSensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )
}

class SimplePresenter : DisplayContract.Presenter {
    private val view: DisplayContract.View = DisplayPage(this)

    private val matches = mutableMapOf<RecognizerMatch, Boolean>()

    fun recognizeMatches() {
        view.patternSelectionContainerVisible = false
        view.resultVisible = false
        view.resultText = ""
        onInputChanges(view.getTextInput())
    }

    override fun onInputChanges(newInput: String) {
        matches.clear()
        if (newInput.isBlank()) {
            view.patternSelectionContainerVisible = false
            view.resultVisible = false
            view.resultText = ""
        } else {
            matches.putAll(RecognizerMatch.recognize(newInput)
                .map { it to false }
                .toMap())

            view.showText(newInput)
            view.showResults(matches.keys)
            view.patternSelectionContainerVisible = true
        }
    }

    override fun onSuggestionClick(match: RecognizerMatch) {
        if (deactivatedMatches.contains(match)) {
            return
        }
        // determine selected state of the match
        matches[match] = !(matches[match]?:false)
        // (de)select match in UI
        view.select(match, matches[match]?:false)
        // find disabled matches
        val disabledMatches = deactivatedMatches
        // disable matches in UI
        matches.keys.forEach { view.disable(it, disabledMatches.contains(it)) }

        computeOutputPattern()
    }

    override fun onOptionsChange(options: RecognizerCombiner.Options) {
        computeOutputPattern()
    }

    private fun computeOutputPattern() {
        view.resultVisible = matches.values.any { it }

        val result = RecognizerCombiner.combine(view.getTextInput(), matches.filter { it.value }.map { it.key }.toList(), view.options)
        view.resultText = result.pattern
    }

    private val deactivatedMatches: Collection<RecognizerMatch> get() {
        val selectedMatches = matches.filter { it.value }.map { it.key }.toList()
        return matches.keys
            .filter { !selectedMatches.contains(it) }
            .filter { match ->
                selectedMatches.any { selectedMatch ->
                    selectedMatch.range.intersect(match.range).isNotEmpty() } }
            .toSet()
    }
}