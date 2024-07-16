package org.olafneumann.regex.generator.ui.parts

import kotlinx.html.a
import kotlinx.html.h5
import kotlinx.html.i
import kotlinx.html.id
import org.olafneumann.regex.generator.js.animate
import org.olafneumann.regex.generator.js.asJQuery
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLElement

abstract class NumberedExpandablePart(
    elementId: String,
    number: Int = numberGenerator.next,
    caption: String,
    initialStateOpen: Boolean = false,
) : NumberedPart(
    elementId = elementId,
    number = number,
    caption = caption,
    leftCaptionElement = { cap ->
        a(classes = "btn p-0 text-start w-100") {
            id = getOpenLinkId(number)
            h5 {
                +cap
                i(classes = "bi bi-chevron-up")
            }
        }
    }
) {
    private val openLinkId = getOpenLinkId(number)

    init {
        HtmlHelper.getElementById<HTMLElement>(openLinkId)
            .onclick = {
                it.preventDefault()
                toggleVisibility()
            }

        if (!initialStateOpen) {
            toggleVisibility(open = false)
        }
    }

    private val isOpen: Boolean
        get() = contentElement.asJQuery().`is`(":visible")

    private fun toggleVisibility(open: Boolean = !isOpen) {
        if (open) {
            contentElement.asJQuery().slideDown()
            headingElement.classList.toggle("rg-upside-down", false)
            numberElement.asJQuery().animate("margin-top" to 0, "margin-bottom" to 0)
        } else {
            contentElement.asJQuery().slideUp()
            headingElement.classList.toggle("rg-upside-down", true)
            numberElement.asJQuery().animate("margin-top" to "-0.32em", "margin-bottom" to "-1em")
        }
    }


    companion object {
        private fun getOpenLinkId(number: Int): String = "rg_link_open_$number"
    }
}
