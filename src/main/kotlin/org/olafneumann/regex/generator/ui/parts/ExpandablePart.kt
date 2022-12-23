package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.html.dom.create
import kotlinx.html.js.i
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.HtmlHelper.getFirstChild
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import kotlin.js.json

abstract class ExpandablePart(
    mainDivId: String
) {
    private val mainDiv: HTMLDivElement = HtmlHelper.getElementById(mainDivId)
    private val numberElement: HTMLElement =
        mainDiv.firstElementChild?.firstElementChild!! as HTMLElement
    private val expandButtonElement: HTMLButtonElement =
        mainDiv.getFirstChild { it is HTMLButtonElement } as HTMLButtonElement
    private val headingElement: HTMLElement = expandButtonElement
        .firstElementChild!! as HTMLElement
    private val expandImageElement: HTMLElement =
        document.create.i(classes = "bi bi-chevron-up")
    private val content: HTMLElement =
        (expandButtonElement.nextElementSibling ?: throw Exception("No content element found.")) as HTMLElement

    init {
        console.log(numberElement)
        headingElement?.appendChild(expandImageElement)

        expandButtonElement.onclick = {
            it.preventDefault()
            toggleVisibility(open = !isOpen)
        }
    }

    private val isOpen: Boolean
        get() = jQuery(content).`is`(":visible")

    protected fun toggleVisibility(open: Boolean = false) {
        val jContent = jQuery(content)
        if (open) {
            jContent.slideDown()
            expandImageElement.classList.toggle("rg-upside-down", false)
            numberElement?.let { jQuery(it) }
                ?.animate(json("margin-top" to 0, "margin-bottom" to 0))
        } else {
            jContent.slideUp()
            expandImageElement.classList.toggle("rg-upside-down", true)
            numberElement?.let { jQuery(it) }
                ?.animate(json("margin-top" to "-0.32em", "margin-bottom" to "-1em"))
        }
    }
}