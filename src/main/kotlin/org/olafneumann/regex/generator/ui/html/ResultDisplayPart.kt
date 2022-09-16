package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.dom.create
import kotlinx.html.js.span
import kotlinx.html.title
import org.olafneumann.regex.generator.js.Prism
import org.olafneumann.regex.generator.js.copyToClipboard
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.UrlGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RegularExpression
import org.olafneumann.regex.generator.ui.DisplayContract
import org.olafneumann.regex.generator.ui.HtmlHelper
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.LanguageCard
import org.olafneumann.regex.generator.ui.LinkHandler
import org.olafneumann.regex.generator.ui.TextHandler
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

internal class ResultDisplayPart(
    private val view: DisplayContract.View,
    private val presenter: DisplayContract.Controller
) {
    private val resultDisplay = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_RESULT_DISPLAY)
    private val buttonCopy = HtmlHelper.getElementById<HTMLButtonElement>(HtmlView.ID_BUTTON_COPY)
    private val containerLanguages = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_DIV_LANGUAGES)
    private val buttonShareLink = HtmlHelper.getElementById<HTMLButtonElement>(HtmlView.ID_BUTTON_SHARE_LINK)

    private var currentPattern = ""

    init {
        buttonCopy.onclick = { copyCurrentPatternToClipboard() }
        buttonShareLink.onclick = { copyToClipboard(textShare.text) }
    }

    private val textShare = TextHandler(
        HtmlHelper.getElementById(HtmlView.ID_TEXT_SHARE_LINK),
        UrlGenerator("ShareLink", "https://regex-generator.olafneumann.org/?sampleText=%1\$s&flags=%2\$s")
    )

    private val anchorRegex101 = LinkHandler(
        HtmlHelper.getElementById(HtmlView.ID_ANCHOR_REGEX101),
        UrlGenerator("Regex101", "https://regex101.com/?regex=%1\$s&flags=g%2\$s&delimiter=/")
    )
    private val anchorRegexr = LinkHandler(
        HtmlHelper.getElementById(HtmlView.ID_ANCHOR_REGEXR),
        UrlGenerator("Regexr", "https://regexr.com/?expression=%1\$s&text=")
    )

    private val languageDisplays = CodeGenerator.all
        .associateWith { LanguageCard(it, containerLanguages) }

    fun showResultingPattern(regex: RegularExpression) {
        showResultRegex(regex)

        // update share-link
        textShare.setPattern(view.inputText, view.options, presenter.selectedRecognizerMatches)

        // update links
        currentPattern = regex.finalPattern
        anchorRegex101.setPattern(currentPattern, view.options)
        anchorRegexr.setPattern(currentPattern, view.options)

        // update programming languages
        val options = view.options
        CodeGenerator.all
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(currentPattern, options)) }
        Prism.highlightAll()
    }

    private fun showResultRegex(regex: RegularExpression) {
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

    private fun copyCurrentPatternToClipboard() = copyToClipboard(text = currentPattern)

    fun hideCopyButton() {
        jQuery(buttonCopy).parent().remove()
    }
}
