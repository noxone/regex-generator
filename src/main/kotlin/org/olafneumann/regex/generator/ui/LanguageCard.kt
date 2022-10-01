package org.olafneumann.regex.generator.ui

import kotlinx.browser.document
import kotlinx.html.ButtonType
import kotlinx.html.button
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.p
import kotlinx.html.pre
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.CodeGenerator.GeneratedSnippet
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

internal class LanguageCard(
    private val codeGenerator: CodeGenerator,
    parent: Node
) {
    private val bodyElementId: String get() = "${codeGenerator.uniqueName}_body"
    private val codeElementId: String get() = "${codeGenerator.uniqueName}_code"
    private val bodyElement: HTMLElement
    private val codeElement: HTMLElement
    private val warnings: MutableList<HTMLElement> = mutableListOf()

    init {
        parent.appendChild(document.create.div(classes = "accordion-item") {
            div(classes = "accordion-header") {
                button(classes = "accordion-button ${if (!shown) "collapsed" else ""}", type = ButtonType.button) {
                    attributes["data-bs-toggle"] = "collapse"
                    attributes["data-bs-target"] = "#$bodyElementId"
                    +codeGenerator.languageName
                }
            }
            div(classes = "accordion-collapse collapse ${if (shown) "show" else ""}") {
                id = bodyElementId
                pre {
                    code(classes = "language-${codeGenerator.highlightLanguage}") {
                        id = codeElementId
                    }
                }
            }
        })
        codeElement = document.getElementById(codeElementId) as HTMLElement
        bodyElement = document.getElementById(bodyElementId) as HTMLElement

        jQuery(bodyElement).on("shown.bs.collapse") { shown = true }
        jQuery(bodyElement).on("hidden.bs.collapse") { shown = false }
    }

    private var shown: Boolean
        get() = ApplicationSettings.isLanguageExpanded(codeGenerator.uniqueName)
        set(value) = ApplicationSettings.setLanguageExpanded(codeGenerator.uniqueName, value)

    fun setSnippet(snippet: GeneratedSnippet) {
        code = snippet.snippet
        warnings.forEach { it.parentElement?.removeChild(it) }
        warnings.clear()
        snippet.warnings.forEach {
            val warningElement = createWarning(it)
            warnings.add(warningElement)
            bodyElement.prepend(warningElement)
        }
    }

    private fun createWarning(text: String): HTMLElement =
        document.create.p(classes = "alert alert-warning rounded m-2") { +text }

    private var code: String
        get() = codeElement.innerHTML
        set(value) {
            codeElement.innerHTML = value.escapeHTML()
        }

    private fun String.escapeHTML(): String {
        val text: String = this@escapeHTML
        if (text.isEmpty()) return text

        return buildString(length) {
            for (element in text) {
                when (val ch: Char = element) {
                    '\'' -> append("&apos;")
                    '\"' -> append("&quot")
                    '&' -> append("&amp;")
                    '<' -> append("&lt;")
                    '>' -> append("&gt;")
                    else -> append(ch)
                }
            }
        }
    }
}
