package org.olafneumann.regex.generator.js

import org.w3c.dom.HTMLElement
import kotlin.js.json

@Suppress("LongParameterList")
class Popover(
    element: HTMLElement,
    private val container: String = "body",
    private val contentString: String? = null,
    private val contentElement: HTMLElement? = null,
    private val html: Boolean = false,
    private val placement: String = "right",
    private val title: String = "",
    private val trigger: String = "click",
    onShown: () -> Unit = {}
) {
    private var jquery: JQuery

    init {
        jquery = jQuery(element)
        jquery.popover(createOptionsJson())
        jquery.on("shown.bs.popover", onShown)
    }

    fun show() = jquery.popover("show")
    fun hide() = jquery.popover("hide")
    fun dispose() = jquery.popover("dispose")
    fun toggle() = jquery.popover("toggle")

    private fun createOptionsJson()
            = json(
        "container" to container,
        "content" to (contentString ?: contentElement),
        "html" to html,
        "placement" to placement,
        "title" to title,
        "trigger" to trigger
    )
}
