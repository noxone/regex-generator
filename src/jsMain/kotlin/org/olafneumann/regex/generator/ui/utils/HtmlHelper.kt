package org.olafneumann.regex.generator.ui.utils

import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.js.JQuery
import org.olafneumann.regex.generator.js.jQuery
import org.w3c.dom.Element
import org.w3c.dom.get
import kotlin.math.max

internal object HtmlHelper {
    @Suppress("kotlin:S6531" /* false positive */)
    internal inline fun <reified T : HTMLElement> getElementById(id: String): T {
        try {
            return document.getElementById(id) as T
        } catch (e: ClassCastException) {
            throw RegexGeneratorException("Unable to find element with id '${id}'.", e)
        }
    }

    internal fun HTMLElement.listChildElements(): List<Element> {
        val children = this.children
        return (0 until this.childElementCount).mapNotNull { children[it] }
    }

    internal fun getHeight(elements: JQuery): Int {
        val previousCss = elements.attr("style")
        elements.css("position:absolute;visibility:hidden;display:block !important;")
        var maxHeight = 0
        elements.each { jq -> maxHeight = max(maxHeight, jq.height()) }
        elements.attr("style", previousCss ?: "")
        return maxHeight
    }

    private fun JQuery.each(function: (JQuery) -> Unit) =
        each { _, htmlElement -> function(jQuery(htmlElement)) }
}
