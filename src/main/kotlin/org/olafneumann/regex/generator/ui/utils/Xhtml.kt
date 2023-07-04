package org.olafneumann.regex.generator.ui.utils

import kotlinx.html.BUTTON
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.button
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.events.Event

fun DIV.gButton(
    classes: String,
    title: String,
    onClickFunction: (Event) -> Unit,
    block: BUTTON.() -> Unit
) {
    button(classes = "btn btn-light btn-sm $classes", type = ButtonType.button) {
        block()
        this.title = title
        this.onClickFunction = onClickFunction
    }
}
