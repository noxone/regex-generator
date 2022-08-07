package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.document
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
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.DisplayContract
import org.olafneumann.regex.generator.ui.HtmlHelper
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MatchPresenter
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import kotlin.js.json

internal class RecognizerDisplayPart(
    private val presenter: DisplayContract.Controller
) {
    private val textDisplay = HtmlHelper.getElementById<HTMLElement>(HtmlView.ID_TEXT_DISPLAY)
    private val rowContainer = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_ROW_CONTAINER)

    // Stuff needed to display the regex
    private val matchPresenterToRowIndex = mutableMapOf<MatchPresenter, Int>()
    private var inputCharacterSpans = listOf<HTMLSpanElement>()

    fun showInputText(inputText: String) {
        textDisplay.clear()
        inputCharacterSpans = inputText.map { document.create.span(classes = "rg-char") { +it.toString() } }.toList()
        inputCharacterSpans.forEach { textDisplay.appendChild(it) }
    }

    fun showMatchingRecognizers(inputText: String, matches: Collection<MatchPresenter>) {
        showInputText(inputText)

        // TODO remove CSS class iterator
        val indices = mutableMapOf<Int, Int>()
        fun nextCssClass(row: Int): String {
            indices[row] = (indices[row] ?: row) + 1
            return HtmlView.MATCH_PRESENTER_CSS_CLASS[indices[row]!! % HtmlView.MATCH_PRESENTER_CSS_CLASS.size]
        }


        rowContainer.clear()
        matchPresenterToRowIndex.clear()

        // find the correct row for each match
        matchPresenterToRowIndex.putAll(distributeToRows(matches))
        // Create HTML elements
        val rowElements = mutableMapOf<Int, HTMLDivElement>()
        matchPresenterToRowIndex.forEach { (matchPresenter, rowIndex) ->
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
        rowContainer.appendChild(document.create.div(classes = HtmlView.CLASS_MATCH_ROW)) as HTMLDivElement

    private fun createMatchPresenterElement(matchPresenter: MatchPresenter): HTMLDivElement =
        document.create.div(classes = HtmlView.CLASS_MATCH_ITEM) {
            div(classes = "rg-match-item-overlay") {
                matchPresenter.recognizerMatches.forEach { match ->
                    div(classes = "rg-recognizer") {
                        if (match.patterns.size == 1) {
                            title = match.patterns[0]
                        } else {
                            title = "Match consisting of ${match.patterns.size} patterns."
                        }
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
        element.classList.toggle(HtmlView.CLASS_ITEM_SELECTED, matchPresenter.selected)
        element.classList.toggle(HtmlView.CLASS_ITEM_NOT_AVAILABLE, matchPresenter.deactivated)
        matchPresenter.onSelectedChanged = { selected ->
            element.classList.toggle(HtmlView.CLASS_ITEM_SELECTED, selected)
            matchPresenter.forEachIndexInRanges { index ->
                inputCharacterSpans[index].classList.toggle(HtmlView.CLASS_CHAR_SELECTED, selected)
            }
        }
        matchPresenter.onDeactivatedChanged =
            { deactivated -> element.classList.toggle(HtmlView.CLASS_ITEM_NOT_AVAILABLE, deactivated) }
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
        HtmlView.MAGIC_HEIGHT + (rows.map { computeMatchPresenterTotalHeight(it.key, it.value) }
            .maxOrNull() ?: 0)

    private fun computeMatchPresenterTotalHeight(rowIndex: Int, matchPresenterElement: HTMLDivElement): Int {
        val jqMatchPresenterElement = jQuery(matchPresenterElement)
        val matchPresenterHeight = jqMatchPresenterElement.height()
        val overlayHeight = HtmlHelper.getHeight(jqMatchPresenterElement.find(".rg-match-item-overlay"))
        return matchPresenterHeight * (rowIndex + 1) + overlayHeight
    }

    companion object {
        private fun Int.toCharacterUnits() = "${this}ch"
    }
}
