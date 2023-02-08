package org.olafneumann.regex.generator.ui.components

import kotlinx.html.ButtonType
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.html.title
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

fun TagConsumer<HTMLElement>.bsButton(
    label: String,
    tooltip: String? = null,
    colorClass: String = "primary",
    id: String? = null,
    onClickFunction: (Event) -> Unit = {}
): HTMLButtonElement = returningRoot {
    button(classes = "btn btn-$colorClass", type = ButtonType.button) {
        +label
        tooltip?.let { title = it }
        id?.let { this.id = it }
        this.onClickFunction = onClickFunction
    }
}

fun TagConsumer<HTMLElement>.closeButton(
    tooltip: String? = null,
    additionalAttributes: List<Pair<String, String>> = emptyList(),
    onClickFunction: (Event) -> Unit = {}
): HTMLButtonElement = returningRoot {
    button(classes = "btn-close", type = ButtonType.button) {
        additionalAttributes.forEach {
            attributes[it.first] = it.second
        }
        attributes["aria-label"] = "Close"
        tooltip?.let { this.title = it }
        this.onClickFunction = onClickFunction
    }
}
