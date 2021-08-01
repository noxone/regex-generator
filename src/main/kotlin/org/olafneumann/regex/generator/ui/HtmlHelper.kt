package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import kotlinx.browser.window
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.js.JQuery
import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.ui.HtmlView.Companion.each
import org.w3c.dom.url.URL
import kotlin.math.max

internal object HtmlHelper {
    internal inline fun <reified T : HTMLElement> getElementById(id: String): T {
        try {
            return document.getElementById(id) as T
        } catch (e: ClassCastException) {
            throw RegexGeneratorException("Unable to find element with id '${id}'.", e)
        }
    }

    internal fun getHeight(elements: JQuery): Int {
        val previousCss = elements.attr("style")
        elements.css("position:absolute;visibility:hidden;display:block !important;")
        var maxHeight = 0
        elements.each { jq -> maxHeight = max(maxHeight, jq.height()) }
        elements.attr("style", previousCss ?: "")
        return maxHeight
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
        updateDocumentSearchQuery(url)
    }

    private fun updateDocumentSearchQuery(url: URL) {
        window.history.replaceState(data = null, title = document.title, url = url.toString())
    }

    val text get() = element.innerText
}
