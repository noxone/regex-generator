package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.CodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.removeClass

const val ELEMENT_DIV = "div"
const val ELEMENT_BUTTON = "button"
const val ELEMENT_PRE = "pre"
const val ELEMENT_CODE = "code"

object HtmlHelper {
    internal fun getDivById(id: String): HTMLDivElement {
        try {
            return document.getElementById(id) as HTMLDivElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find div with id '$id'.", e)
        }
    }

    internal fun getInputById(id: String): HTMLInputElement {
        try {
            return document.getElementById(id) as HTMLInputElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find input with id '$id'.", e)
        }
    }

    internal fun getButtonById(id: String): HTMLButtonElement {
        try {
            return document.getElementById(id) as HTMLButtonElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find button with id '$id'.", e)
        }
    }

    internal fun getAnchorById(id: String): HTMLAnchorElement {
        try {
            return document.getElementById(id) as HTMLAnchorElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find link with id '$id'.", e)
        }
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