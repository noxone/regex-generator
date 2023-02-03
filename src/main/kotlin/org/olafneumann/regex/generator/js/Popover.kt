package org.olafneumann.regex.generator.js

import kotlinx.browser.document
import kotlinx.html.ButtonType
import kotlinx.html.button
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.HTMLElement
import kotlin.js.json

@Suppress("LongParameterList")
class Popover(
    element: HTMLElement,
    private val container: String = "body",
    private val contentString: String? = null,
    private val contentElement: HTMLElement? = null,
    private val html: Boolean = false,
    private val placement: Placement = Placement.Right,
    private val title: String = "",
    private val trigger: String = "click",
    onShown: () -> Unit = {},
    private val onCloseButtonClick: Popover.() -> Unit = { dispose() },
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

    private fun createOptionsJson() = json(
        "container" to container,
        "content" to (contentString ?: contentElement),
        "html" to html,
        "placement" to placement.value,
        "title" to document.create.div(classes = "d-flex justify-content-between align-items-center") {
            +this@Popover.title
            button(classes = "btn-close", type = ButtonType.button) {
                attributes["aria-label"] = "Close"
                title = "Cancel"
                onClickFunction = { onCloseButtonClick() }
            }
        },
        "trigger" to trigger
    )

    enum class Placement(
        val value: String
    ) {
        Top("top"),
        Bottom("bottom"),
        Left("left"),
        Right("right")
    }
}
