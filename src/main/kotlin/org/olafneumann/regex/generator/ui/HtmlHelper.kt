package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.w3c.dom.url.URL

internal object HtmlHelper {
    internal inline fun <reified T : HTMLElement> getElementById(id: String): T {
        try {
            return document.getElementById(id) as T
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find element with id '${id}'.", e)
        }
    }
}

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
    var updateSearchPattern = false

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
        if (updateSearchPattern) {
            updateDocumentSearchQuery(url, pattern)
        }
    }

    private fun updateDocumentSearchQuery(url: URL, sampleText: String) {
        val title = "Regex Generator \"${sampleText}\""
        document.title = title
        window.history.replaceState(data = null, title = title, url = url.toString())
    }

    val text get() = element.innerText
}
