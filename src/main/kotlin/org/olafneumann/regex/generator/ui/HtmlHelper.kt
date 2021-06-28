package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.regex.RecognizerMatch

internal object HtmlHelper {
    internal inline fun <reified T : HTMLElement> getElementById(id: String): T {
        try {
            return document.getElementById(id) as T
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find element with id '$id'.", e)
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
    fun setPattern(pattern: String, options: RecognizerCombiner.Options, selectedMatches: Collection<RecognizerMatch>) {
        val selection = selectedMatches.map { "${it.ranges[0].first}|${it.recognizer.name}" }
            .joinToString(separator = ",") { encodeURIComponent(it) }
        element.innerText = "${codeGenerator.generateCode(pattern, options).snippet}&onlyPatterns=${options.onlyPatterns}&matchWholeLine=${options.matchWholeLine}&selection=${selection}"
    }

    val text get() = element.innerText
}
