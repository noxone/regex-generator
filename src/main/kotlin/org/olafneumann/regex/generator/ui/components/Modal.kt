package org.olafneumann.regex.generator.ui.components

import kotlinx.browser.document
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.h5
import kotlinx.html.js.div
import org.olafneumann.regex.generator.js.toJson
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.properties.Delegates

private fun TagConsumer<HTMLElement>.modal(
    title: String,
    buttons: List<Button> = emptyList(),
    mainBlock: TagConsumer<HTMLElement>.() -> Unit = {},
    getModal: () -> Modal,
): HTMLElement {
    var element: HTMLElement by Delegates.notNull()
    element = returningRoot {
        div(classes = "modal modal-xl fade") {
            onModalHiddenFunction = { element.remove() }
            div(classes = "modal-dialog modal-dialog-centered modal-dialog-scrollable") {
                div(classes = "modal-content") {
                    div(classes = "modal-header") {
                        h5(classes = "modal-title") { +title }
                        closeButton(additionalAttributes = listOf("data-bs-dismiss" to "modal"))
                    }
                    div(classes = "modal-body") {
                        mainBlock()
                    }
                    div(classes = "modal-footer") {
                        buttons.forEach { button ->
                            bsButton(label = button.title, colorClass = button.colorClass, onClickFunction = { event ->
                                button.onClickFunction.invoke(getModal(), event.unsafeCast<Event>())
                            })
                        }
                    }
                }
            }
        }
    }
    return element
}

data class Button(
    val title: String,
    val colorClass: String = "primary",
    val onClickFunction: Modal.(Event) -> Unit
)

fun createModal(
    title: String,
    buttons: List<Button> = emptyList(),
    mainBlock: TagConsumer<HTMLElement>.() -> Unit = {}
): Modal {
    var modal: Modal by Delegates.notNull()
    val element = document.create.modal(title = title, buttons = buttons, mainBlock = mainBlock, getModal = { modal })
    modal = createModal(element = element)
    return modal
}

@Suppress("UnusedPrivateMember") // both members are used in JS code
private fun createModal(element: HTMLElement, static: Boolean = true): Modal {
    val options = mapOf(
        "backdrop" to if (static) "static" else null,
        "focus" to true,
        "keyboard" to true
    )
        .toJson()
    val modal = js("new bootstrap.Modal(element, options)")
    return modal.unsafeCast<Modal>()
}

external class Modal {
    fun show()
    fun hide()
}

var CommonAttributeGroupFacade.onModalHiddenFunction: (Event) -> Unit
    get() = throw UnsupportedOperationException("You can't read variable onInput")
    set(newValue) {
        consumer.onTagEvent(
            this,
            "hidden.bs.modal",
            newValue.unsafeCast<(kotlinx.html.org.w3c.dom.events.Event) -> Unit>()
        )
    }

