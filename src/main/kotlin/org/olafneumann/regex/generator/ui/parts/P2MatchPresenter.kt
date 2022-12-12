package org.olafneumann.regex.generator.ui.parts

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
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.model.MatchPresenter
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.asList
import kotlin.js.json

class P2MatchPresenter(
    private val controller: MVCContract.Controller
) {
    private val textDisplay = HtmlHelper.getElementById<HTMLElement>(HtmlView.ID_TEXT_DISPLAY)
    private val rowContainer = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_ROW_CONTAINER)

    private var inputCharacterSpans = listOf<HTMLSpanElement>()

    fun applyModel(model: DisplayModel) {
        showText(model.patternRecognizerModel.input)
        showMatchPresenters(model.rowsOfMatchPresenters, model.patternRecognizerModel.selectedRecognizerMatches)
    }

    fun showText(text: String) {
        textDisplay.clear()
        inputCharacterSpans = text.map { document.create.span(classes = "rg-char") { +it.toString() } }.toList()
        inputCharacterSpans.forEach { textDisplay.appendChild(it) }

    }

    private fun showMatchPresenters(
        presenters: List<List<MatchPresenter>>,
        selectedRecognizerMatches: Collection<RecognizerMatch>
    ) {
        val isSelected: (RecognizerMatch) -> Boolean = { selectedRecognizerMatches.contains(it) }
        inputCharacterSpans.forEachIndexed { index, htmlSpanElement ->
            val selected = selectedRecognizerMatches.any { it.contains(index) }
            htmlSpanElement.classList.toggle(HtmlView.CLASS_CHAR_SELECTED, selected)
        }

        rowContainer.clear()

        presenters.forEachIndexed { rowIndex, row ->
            val rowElement = createRowElement()
            rowContainer.appendChild(rowElement)
            row.forEachIndexed { index, matchPresenter ->
                val matchPresenterElement = createPresenterElement(matchPresenter, isSelected)
                rowElement.appendChild(matchPresenterElement)

                val cssClass = getColorClass(rowIndex, index)
                matchPresenterElement.addClass(cssClass)
                if (matchPresenter.recognizerMatches.all { it.recognizer.isDerived }) {
                    matchPresenterElement.addClass("rg-match-item-stripe")
                }
                matchPresenterElement.style.left = matchPresenter.first.toCharacterUnits()
                matchPresenterElement.style.width = matchPresenter.length.toCharacterUnits()
                if (matchPresenter.ranges.size == 2) {
                    matchPresenterElement.style.borderLeftWidth =
                        (matchPresenter.ranges[0].last - matchPresenter.ranges[0].first + 1).toCharacterUnits()
                    matchPresenterElement.style.borderRightWidth =
                        (matchPresenter.ranges[1].last - matchPresenter.ranges[1].first + 1).toCharacterUnits()
                }
                matchPresenterElement.classList.toggle(HtmlView.CLASS_ITEM_SELECTED, matchPresenter.selected)
                matchPresenterElement.classList.toggle(HtmlView.CLASS_ITEM_NOT_AVAILABLE, matchPresenter.deactivated)

                applyListenersForUserInput(matchPresenter, matchPresenterElement, cssClass)
            }
        }

        animateResultDisplaySize(rowElements = rowContainer.children.asList().map { it as HTMLDivElement })
    }

    private fun createRowElement(): HTMLDivElement =
        document.create.div(classes = HtmlView.CLASS_MATCH_ROW)

    private fun createPresenterElement(
        matchPresenter: MatchPresenter,
        isSelected: (RecognizerMatch) -> Boolean
    ): HTMLDivElement =
        document.create.div(classes = HtmlView.CLASS_MATCH_ITEM) {
            div(classes = "rg-match-item-overlay") {
                matchPresenter.recognizerMatches.forEach { recognizerMatch ->
                    div(classes = "rg-recognizer") {
                        title = if (recognizerMatch.patterns.size == 1) {
                            recognizerMatch.patterns[0]
                        } else {
                            "Match consisting of ${recognizerMatch.patterns.size} patterns."
                        }
                        a {
                            +recognizerMatch.title
                            onClickFunction = { event ->
                                controller.onRecognizerMatchClick(recognizerMatch)
                                event.stopPropagation()
                            }
                        }
                    }
                }
            }
            onClickFunction = {
                when {
                    matchPresenter.selected ->
                        controller.onRecognizerMatchClick(matchPresenter.recognizerMatches.first { isSelected(it) })
                    matchPresenter.recognizerMatches.size == 1 ->
                        controller.onRecognizerMatchClick(matchPresenter.recognizerMatches.first())
                }
            }
        }

    private fun getColorClass(row: Int, index: Int): String {
        return HtmlView.MATCH_PRESENTER_CSS_CLASS[(row + index) % HtmlView.MATCH_PRESENTER_CSS_CLASS.size]
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

    private inline val MatchPresenter.availableForHighlight: Boolean get() = !deactivated && !selected

    private fun animateResultDisplaySize(rowElements: List<HTMLDivElement>) {
        val newHeight = "${computeMatchPresenterAreaHeight(rowElements)}px"
        val jqRowContainer = jQuery(rowContainer)
        jqRowContainer.stop()
        jqRowContainer.animate(json("height" to newHeight), duration = 350)
    }

    private fun computeMatchPresenterAreaHeight(rowElements: List<HTMLDivElement>): Int =
        HtmlView.MAGIC_HEIGHT + (rowElements
            .mapIndexed { index, htmlDivElement -> computeMatchPresenterTotalHeight(index, htmlDivElement) }
            .maxOrNull() ?: 0)

    private fun computeMatchPresenterTotalHeight(rowIndex: Int, matchPresenterElement: HTMLDivElement): Int {
        val jqMatchPresenterElement = jQuery(matchPresenterElement)
        val matchPresenterHeight = jqMatchPresenterElement.height()
        val overlayHeight = HtmlHelper.getHeight(jqMatchPresenterElement.find(".rg-match-item-overlay"))
        return matchPresenterHeight * (rowIndex + 1) + overlayHeight
    }

    private fun Int.toCharacterUnits() = "${this}ch"
}
