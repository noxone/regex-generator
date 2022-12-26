package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.DIV
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.id
import kotlinx.html.injector.InjectByClassName
import kotlinx.html.injector.inject
import kotlinx.html.js.div
import org.olafneumann.regex.generator.js.asJQuery
import org.olafneumann.regex.generator.main
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.HtmlHelper.listChildElements
import org.olafneumann.regex.generator.util.IdGenerator
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import kotlin.properties.Delegates

abstract class NumberedPart(
    elementId: String,
    protected val number: Int = numberGenerator.next,
    caption: String,
    leftCaptionElement: DIV.(caption: String) -> Unit = { caption -> h5 { +caption } },
    rightCaptionElement: DIV.() -> Unit = {}
) {
    private val elements = Elements()
    private val mainDiv = document.create.inject(elements, listOf(
        InjectByClassName("rg-number") to Elements::number,
        InjectByClassName("rg-caption-holder") to Elements::caption,
        InjectByClassName("rg-content") to Elements::content,
    )).div(classes = "row bg-light rounded mt-3 pr-4") {
        id = elementId
        div (classes = "col-sm-1 p-4 d-none d-sm-none d-md-block text-center") {
            h3(classes = "display-3 text-secondary rg-number") {
                +number.toString()
            }
        }
        div(classes = "col-12 col-md-11 py-4 pl-4") {
            div(classes = "d-flex justify-content-between rg-caption-holder") {
                leftCaptionElement(caption)
                rightCaptionElement()
            }
            div(classes = "rg-content") {
                // content taking from HTML page
            }
        }
    }

    protected val numberElement: HTMLElement get() = elements.number
    protected val headingElement: HTMLElement get() = elements.caption
    protected val contentElement: HTMLElement get() = elements.content

    init {
        val originalElement = HtmlHelper.getElementById<HTMLElement>(elementId)
        val root = originalElement.parentElement!!
        root.insertBefore(mainDiv, originalElement)
        root.removeChild(originalElement)
        originalElement.listChildElements().forEach { elements.content.appendChild(it) }
    }

    private class Elements {
        var number: HTMLHeadingElement by Delegates.notNull()
        var caption: HTMLElement by Delegates.notNull()
        var content: HTMLDivElement by Delegates.notNull()
    }

    companion object {
        protected val numberGenerator = IdGenerator()
    }
}