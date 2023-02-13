package org.olafneumann.regex.generator.ui.utils

import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.js.Date

class RageClickDetector private constructor(
    private val thresholdCount: Int,
    private val thresholdTime: Int,
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
        clicks.removeAll { now - it > thresholdTime }
    }

    private val hasRangeClicks: Boolean get() = clicks.size > thresholdCount

    private fun triggerRageClickEvent(event: Event) {
        window.setTimeout({ onRageClick(event) }, 1)
    }

    companion object {
        private const val THRESHOLD_COUNT = 3
        private const val THRESHOLD_TIME_MS = 800

        fun createMouseEventListener(
            thresholdCount: Int = THRESHOLD_COUNT,
            thresholdTime: Int = THRESHOLD_TIME_MS,
            onRageClick: (Event) -> Unit
        ): (MouseEvent) -> Unit {
            val rageClickDetector = RageClickDetector(
                thresholdCount = thresholdCount,
                thresholdTime = thresholdTime,
                onRageClick = onRageClick
            )
            return { rageClickDetector.clickElement(it as Event) }
        }

        fun createEventFunction(
            thresholdCount: Int = THRESHOLD_COUNT,
            thresholdTime: Int = THRESHOLD_TIME_MS,
            onRageClick: (Event) -> Unit
        ): (kotlinx.html.org.w3c.dom.events.Event) -> Unit {
            val rageClickDetector = RageClickDetector(
                thresholdCount = thresholdCount,
                thresholdTime = thresholdTime,
                onRageClick = onRageClick
            )
            return { rageClickDetector.clickElement(it as Event) }
        }
    }
}
