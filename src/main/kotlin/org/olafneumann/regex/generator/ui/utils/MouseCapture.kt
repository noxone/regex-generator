package org.olafneumann.regex.generator.ui.utils

import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.js.json

class MouseCapture(
    private val customMouseUpListener: (MouseEvent) -> Unit,
    private val customMouseMoveListener: (MouseEvent) -> Unit
) {
    companion object {
        private val EVENT_LISTENER_MODE = json("capture" to true)

        fun capture(
            event: MouseEvent,
            mouseUpListener: (MouseEvent) -> Unit = {},
            mouseMoveListener: (MouseEvent) -> Unit = {}
        ) {
            MouseCapture(customMouseUpListener = mouseUpListener, customMouseMoveListener = mouseMoveListener)
                .captureMouseEvents(event)
        }

        fun preventGlobalMouseEvents() {
            js("document.body.style['pointer-events'] = 'none';")
        }

        fun restoreGlobalMouseEvents() {
            js("document.body.style['pointer-events'] = 'auto';")
        }
    }

    private val mouseMoveListener: (Event) -> Unit = { mouseMoveListener(it as MouseEvent) }
    private fun mouseMoveListener(e: MouseEvent) {
        customMouseMoveListener(e)
        e.stopPropagation()
    }

    private val mouseUpListener: (Event) -> Unit = { mouseUpListener(it as MouseEvent) }
    private fun mouseUpListener(e: MouseEvent) {
        customMouseUpListener(e)
        restoreGlobalMouseEvents()
        document.removeEventListener("mouseup", mouseUpListener, EVENT_LISTENER_MODE)
        document.removeEventListener("mousemove", mouseMoveListener, EVENT_LISTENER_MODE)
        e.stopPropagation()
    }

    private fun captureMouseEvents(e: MouseEvent) {
        preventGlobalMouseEvents()
        document.addEventListener("mouseup", mouseUpListener, EVENT_LISTENER_MODE)
        document.addEventListener("mousemove", mouseMoveListener, EVENT_LISTENER_MODE)
        e.preventDefault()
        e.stopPropagation()
    }
}
