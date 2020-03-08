package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.removeClass

private const val ELEMENT_DIV = "div"
private const val ELEMENT_BUTTON = "button"
private const val ELEMENT_PRE = "pre"
private const val ELEMENT_CODE = "code"

internal object HtmlHelper {
    private fun <T : Node> ensureClassCast(id: String, type: String, getter: (String) -> T): T {
        try {
            return getter(id)
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find $type with id '$id'.", e)
        }
    }

    internal fun getDivById(id: String): HTMLDivElement {
        return ensureClassCast(id, "div") { document.getElementById(it) as HTMLDivElement }
    }

    internal fun getInputById(id: String): HTMLInputElement {
        return ensureClassCast(id, "input") { document.getElementById(it) as HTMLInputElement }
    }

    internal fun getButtonById(id: String): HTMLButtonElement {
        return ensureClassCast(id, "button") { document.getElementById(it) as HTMLButtonElement }
    }

    internal fun getAnchorById(id: String): HTMLAnchorElement {
        return ensureClassCast(id, "link") { document.getElementById(it) as HTMLAnchorElement }
    }

    internal fun createDivElement(parent: Node, vararg classNames: String): HTMLDivElement =
        createElement(parent, ELEMENT_DIV, *classNames) as HTMLDivElement

    internal fun createButtonElement(parent: Node, vararg classNames: String): HTMLButtonElement =
        createElement(parent, ELEMENT_BUTTON, *classNames) as HTMLButtonElement

    internal fun createPreElement(parent: Node, vararg classNames: String): HTMLPreElement =
        createElement(parent, ELEMENT_PRE, *classNames) as HTMLPreElement

    internal fun createCodeElement(parent: Node, vararg classNames: String): HTMLElement =
        createElement(parent, ELEMENT_CODE, *classNames) as HTMLElement

    private fun createElement(parent: Node, type: String, vararg classNames: String): Element {
        val element = document.createElement(type)
        element.addClass(*classNames)
        parent.appendChild(element)
        return element
    }

    internal fun toggleClass(element: HTMLElement, selected: Boolean, className: String) {
        if (selected)
            element.addClass(className)
        else
            element.removeClass(className)
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