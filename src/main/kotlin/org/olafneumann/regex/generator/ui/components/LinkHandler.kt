package org.olafneumann.regex.generator.ui.components

import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.ui.HtmlView
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URL

internal class LinkHandler(
    private val link: HTMLAnchorElement,
    private val codeGenerator: CodeGenerator
) {
    fun setPattern(pattern: String, options: RecognizerCombiner.Options) {
        link.href = codeGenerator.generateCode(pattern, options).snippet
    }
}

internal class TextHandler(
    private val element: HTMLElement,
    private val codeGenerator: CodeGenerator
) {
    fun setPattern(pattern: String, options: RecognizerCombiner.Options, selectedMatches: Collection<RecognizerMatch>) {
        val selection = selectedMatches.map { "${it.ranges[0].first}|${it.recognizer.name}" }
            .joinToString(separator = ",") { encodeURIComponent(it) }
        val searchAddition = mapOf(
            HtmlView.SEARCH_ONLY_PATTERNS to options.onlyPatterns,
            HtmlView.SEARCH_MATCH_WHOLE_LINE to options.matchWholeLine,
            HtmlView.SEARCH_SELECTION to selection)
            .map { "${it.key}=${it.value}" }
            .joinToString(separator = "&")

        val shareUrlString = "${codeGenerator.generateCode(pattern, options).snippet}&${searchAddition}"

        val url = URL(shareUrlString)
        url.protocol = window.location.protocol
        url.hostname = window.location.hostname
        url.port = window.location.port

        element.innerText = url.toString()
        updateDocumentSearchQuery(url.search)
    }

    private fun updateDocumentSearchQuery(urlString: String) {
        window.history.replaceState(data = null, title = document.title, url = urlString)
    }

    val text get() = element.innerText
}
