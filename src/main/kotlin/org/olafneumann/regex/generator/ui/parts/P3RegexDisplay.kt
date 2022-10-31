package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.dom.create
import kotlinx.html.js.span
import kotlinx.html.title
import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

class P3RegexDisplay(
    private val controller: MVCContract.Controller
) {
    private val resultDisplay = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_RESULT_DISPLAY)
    private val buttonCopy = HtmlHelper.getElementById<HTMLButtonElement>(HtmlView.ID_BUTTON_COPY)

    init {
        buttonCopy.onclick = { controller.onCopyRegexButtonClick() }
    }

    fun applyModel(model: DisplayModel) {
        showOrHideCopyButton(model.showCopyButton)
        showResultRegex(regex = model.patternRecognitionModel.regularExpression)
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

    private fun showOrHideCopyButton(show: Boolean) {
        buttonCopy.classList.toggle("d-none", show)
    }
}
