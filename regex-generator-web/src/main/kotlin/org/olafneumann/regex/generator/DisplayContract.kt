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
const val ID_REGEX_RESULT_CONTAINER = "rg_regex_result_container"

interface DisplayContract {
    companion object {
        fun organize(findings: List<RecognizerMatch>): List<MatchDisplay> {
            val lines = mutableListOf<Int>()
            return findings.map { finding ->
                val indexOfFreeLine = lines.indexOfFirst { it < finding.range.first }
                val line = if (indexOfFreeLine >= 0) indexOfFreeLine else { lines.add(0); lines.size - 1 }
                val gap = finding.range.first - lines[line]
                lines[line] = finding.range.last + 1
                MatchDisplay(line, gap, finding)
            }
        }
    }

    interface View {
        fun getTextInput(): String
        fun showText(text: String)
        fun showResults(matchDisplays: List<MatchDisplay>)
        fun hideMatchResultContainer()
        fun showMatchResultContainer()
        fun hideRegexResultContainer()
        fun showRegexResultContainer()
    }

    interface Presenter {
        fun onInputChanges(newInput: String)
    }
}

data class MatchDisplay(
    val line: Int,
    val gap: Int,
    val match: RecognizerMatch
)

class DisplayPage(
    private val presenter: DisplayContract.Presenter
) : DisplayContract.View {

    private val input = document.getElementById(ID_INPUT_ELEMENT) as HTMLInputElement
    private val textDisplay = document.getElementById(ID_TEXT_DISPLAY) as HTMLDivElement
    private val rowContainer = document.getElementById(ID_ROW_CONTAINER) as HTMLDivElement
    private val matchResultContainer = document.getElementById(ID_OUTER_RESULT_CONTAINER) as HTMLDivElement
    private val regexResultContainer = document.getElementById(ID_REGEX_RESULT_CONTAINER) as HTMLDivElement

    init {
        input.addEventListener("input", { _ -> presenter.onInputChanges(getTextInput()) })
    }

    override fun getTextInput() = input.value

    override fun showText(text: String) {
        textDisplay.innerHTML = text
    }

    override fun showResults(matchDisplays: List<MatchDisplay>) {
        rowContainer.clear()

        var index = 0;
        val classes = listOf<String>("red", "green", "blue")

        val rows = (0..(matchDisplays.map { it.line }.max()?:0)).map { createRowElement() }.toList()
        matchDisplays.forEach { match ->
            val row = rows[match.line]
            val element = createMatchElement(row)
            element.addClass("rg-match-item-${classes[index++%classes.size]}")
            element.style.width = "${match.match.inputPart.length}ch"
            element.style.marginLeft = "${match.gap}ch"
            element.title = match.match.inputPart
        }
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
            val matchResults = DisplayContract.organize(findings)
            // matchResults.forEach { console.info(it) }

            view.showResults(matchResults)

            view.showMatchResultContainer()
        }
    }

    fun recognizeMatches() {
        onInputChanges(view.getTextInput())
        view.hideMatchResultContainer()
        view.hideRegexResultContainer()
    }
}