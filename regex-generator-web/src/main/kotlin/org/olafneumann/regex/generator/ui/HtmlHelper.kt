package org.olafneumann.regex.generator.ui

import org.w3c.dom.*
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.removeClass

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

    internal fun getLinkById(id: String): HTMLAnchorElement {
        try {
            return document.getElementById(id) as HTMLAnchorElement
        } catch (e: ClassCastException) {
            throw RuntimeException("Unable to find link with id '$id'.", e)
        }
    }

    internal fun createDivElement(parent: Node, vararg classNames: String): HTMLDivElement {
        val element = document.createElement(ELEMENT_DIV) as HTMLDivElement
        element.addClass(*classNames)
        parent.appendChild(element)
        return element
    }

    internal fun toggleClass(element: HTMLDivElement, selected: Boolean, className: String) {
        if (selected)
            element.addClass(className)
        else
            element.removeClass(className)
    }
}