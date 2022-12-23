package org.olafneumann.regex.generator.ui.parts

import kotlinx.html.a
import kotlinx.html.h5
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLElement
import kotlin.js.json

abstract class AbstractExpandablePart(
    elementId: String,
    number: Int,
    caption: String,
    initialStateOpen: Boolean = true,
) : AbstractPart(
    elementId = elementId,
    number = number,
    caption = caption,
    leftCaptionElement = { caption ->
        a(classes = "btn p-0") {
            id = getOpenLinkId(number)
            h5 {
                +caption
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
        get() = jQuery(contentElement).`is`(":visible")

    private fun toggleVisibility(open: Boolean = !isOpen) {
        if (open) {
            jQuery(contentElement).slideDown()
            headingElement.classList.toggle("rg-upside-down", false)
            jQuery(numberElement).animate(json("margin-top" to 0, "margin-bottom" to 0))
        } else {
            jQuery(contentElement).slideUp()
            headingElement.classList.toggle("rg-upside-down", true)
            jQuery(numberElement).animate(json("margin-top" to "-0.32em", "margin-bottom" to "-1em"))
        }
    }


    companion object {
        private fun getOpenLinkId(number: Int): String = "rg_link_open_$number"
    }
}