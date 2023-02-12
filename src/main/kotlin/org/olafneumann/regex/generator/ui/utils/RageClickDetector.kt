package org.olafneumann.regex.generator.ui.utils

import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.js.Date

class RageClickDetector private constructor(
    private val onRageClick: (Event) -> Unit,
) {
    private val clicks: MutableList<Double> = mutableListOf()

    private fun clickElement(event: Event) {
        val now = Date().getTime()
        clicks.add(now)
        removeOlderClicks(now)
        if (hasRangeClicks) {
            clicks.clear()
            triggerRageClickEvent(event)
        }
    }

    private fun removeOlderClicks(now: Double) {
        clicks.removeAll { now - it > THRESHOLD_TIME_MS }
    }

    private val hasRangeClicks: Boolean get() = clicks.size > THRESHOLD_COUNT

    private fun triggerRageClickEvent(event: Event) {
        window.setTimeout({ onRageClick(event) }, 1)
    }

    companion object {
        private const val THRESHOLD_COUNT = 3
        private const val THRESHOLD_TIME_MS = 800

        fun createRageClickDetector(element: HTMLElement, onRageClick: (Event) -> Unit,): RageClickDetector {
            val rageClickDetector = RageClickDetector(onRageClick = onRageClick)
            element.addEventListener("click", rageClickDetector::clickElement)
            return rageClickDetector
        }

        fun createEventListener(onRageClick: (Event) -> Unit): (kotlinx.html.org.w3c.dom.events.Event) -> Unit {
            val rageClickDetector = RageClickDetector(onRageClick = onRageClick)
            return { rageClickDetector.clickElement(it as Event) }
        }
    }
}
