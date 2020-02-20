package org.olafneumann.regex.generator

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.clear
import kotlin.dom.removeClass

const val ID_INPUT_ELEMENT = "rg_raw_input_text"
const val ID_TEXT_DISPLAY = "rg_text_display"
const val ID_ROW_CONTAINER = "rg_row_container"
const val ID_OUTER_RESULT_CONTAINER = "rg_outer_result_container"
const val ID_DETAIL_RESULT_CONTAINER = "rg_regex_detail_container"
const val ID_REGEX_RESULT_CONTAINER = "rg_regex_result_container"

interface DisplayContract {
    companion object {
        fun distributeToLines(findings: List<RecognizerMatch>): List<MatchDisplay> {
            val lines = mutableListOf<Int>()
            return findings.map { finding ->
                val indexOfFreeLine = lines.indexOfFirst { it < finding.range.first }
                val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
                lines[line] = finding.range.last + 1
                MatchDisplay(line, finding)
            }
        }
    }

    interface View {
        fun getTextInput(): String
        fun showText(text: String)
        fun showResults(matchDisplays: List<MatchDisplay>)
        fun hideMatchResultContainer()
        fun showMatchResultContainer()
        fun hideRegexDetailContainer()
        fun showRegexDetailContainer()
        fun hideRegexResultContainer()
        fun showRegexResultContainer()
    }

    interface Presenter {
        fun onInputChanges(newInput: String)
        fun onSuggestionSelection(match: RecognizerMatch, selected: Boolean)
    }
}

data class MatchDisplay(
    val line: Int,
    val match: RecognizerMatch,
    var selected: Boolean = false
)

class DisplayPage(
    private val presenter: DisplayContract.Presenter
) : DisplayContract.View {

    private val input = document.getElementById(ID_INPUT_ELEMENT) as HTMLInputElement
    private val textDisplay = document.getElementById(ID_TEXT_DISPLAY) as HTMLDivElement
    private val rowContainer = document.getElementById(ID_ROW_CONTAINER) as HTMLDivElement
    private val matchResultContainer = document.getElementById(ID_OUTER_RESULT_CONTAINER) as HTMLDivElement
    private val regexDetailContainer = document.getElementById(ID_DETAIL_RESULT_CONTAINER) as HTMLDivElement
    private val regexResultContainer = document.getElementById(ID_REGEX_RESULT_CONTAINER) as HTMLDivElement

    private val recognizerMatchElements = mutableMapOf<RecognizerMatch, HTMLDivElement>()

    init {
        input.addEventListener("input", { _ -> presenter.onInputChanges(getTextInput()) })
    }

    override fun getTextInput() = input.value

    override fun showText(text: String) {
        textDisplay.innerText = text
    }

    override fun showResults(matchDisplays: List<MatchDisplay>) {
        rowContainer.clear()
        recognizerMatchElements.clear()

        var index = 0;
        val classes = listOf<String>("red", "green", "blue")

        val rows = (0..(matchDisplays.map { it.line }.max()?:0)).map { createRowElement() }.toList()
        matchDisplays.forEach { display ->
            val row = rows[display.line]
            val element = createMatchElement(row)
            element.addClass("rg-match-item-${classes[index++%classes.size]}")
            element.style.width = "${display.match.inputPart.length}ch"
            element.style.left = "${display.match.range.first}ch"
            element.title = "${display.match.recognizer.name} (${display.match.inputPart})"
            recognizerMatchElements[display.match] = element
            element.addEventListener("click", { _ -> clickMatchElement(display)})
        }
    }

    private fun clickMatchElement(display: MatchDisplay) {
        display.selected = !display.selected
        if (display.selected) {
            recognizerMatchElements[display.match]?.let { it.addClass("rg-item-selected") }
        } else {
            recognizerMatchElements[display.match]?.let { it.removeClass("rg-item-selected") }
        }
        presenter.onSuggestionSelection(display.match, display.selected)
    }

    private fun createRowElement(): HTMLDivElement = createDivElement(rowContainer, "rg-match-row")

    private fun createMatchElement(parent: HTMLDivElement): HTMLDivElement = createDivElement(parent, "rg-match-item")

    private fun createDivElement(parent: HTMLDivElement, vararg classNames: String): HTMLDivElement {
        val element = document.createElement("div") as HTMLDivElement
        element.addClass(*classNames)
        parent.appendChild(element)
        return element
    }

    override fun showMatchResultContainer() = showElement(matchResultContainer)
    override fun hideMatchResultContainer() = hideElement(matchResultContainer)
    override fun showRegexResultContainer() = showElement(regexResultContainer)
    override fun hideRegexResultContainer() = hideElement(regexResultContainer)
    override fun showRegexDetailContainer() = showElement(regexDetailContainer)
    override fun hideRegexDetailContainer() = hideElement(regexDetailContainer)
    private fun showElement(element: HTMLDivElement) { element.removeClass("invisible") }
    private fun hideElement(element: HTMLDivElement) { element.addClass("invisible") }
}

class SimplePresenter() : DisplayContract.Presenter {
    private val view: DisplayContract.View = DisplayPage(this)

    override fun onInputChanges(newInput: String) {
        if (newInput.isBlank()) {
            view.hideMatchResultContainer()
            view.hideRegexResultContainer()
        } else {
            view.showText(newInput)

            val findings = RecognizerMatch.recognize(input = newInput)
            val matchResults = DisplayContract.distributeToLines(findings)
            // matchResults.forEach { console.info(it) }

            view.showResults(matchResults)

            view.showMatchResultContainer()
        }
    }

    override fun onSuggestionSelection(match: RecognizerMatch, selected: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun recognizeMatches() {
        view.hideMatchResultContainer()
        view.hideRegexDetailContainer()
        view.hideRegexResultContainer()
        onInputChanges(view.getTextInput())
    }
}