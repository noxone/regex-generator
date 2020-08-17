package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import kotlin.properties.Delegates

class MatchPresenter(
    val recognizerMatches: List<RecognizerMatch>,
    selectedMatch: RecognizerMatch? = null,
    deactivated: Boolean = false,
    var onSelectedChanged: (Boolean) -> Unit = {},
    var onDeactivatedChanged: (Boolean) -> Unit = {}
) : HasRange {
    val ranges: List<IntRange>

    init {
        if (recognizerMatches.isEmpty()) {
            throw IllegalArgumentException("Empty MatchPresenter is not allowed.")
        }
        val listOfMatches = recognizerMatches.toList()
        for (i in 1 until listOfMatches.size) {
            if (!listOfMatches[0].hasSameRangesAs(listOfMatches[i])) {
                throw IllegalArgumentException("All RecognizerMatches in a MatchPresenter must have the same ranges.")
            }
        }
        ranges = listOfMatches[0].ranges
    }

    override val first = ranges.first().first
    override val last = ranges.last().last
    override val length = last - first + 1

    val priority = recognizerMatches.map { it.priority }.maxOrNull()!!

    //private var _selected: Boolean by Delegates.observable(selected) { _, _, new -> onSelectedChanged(new) }
    var selectedMatch: RecognizerMatch? by Delegates.observable(selectedMatch)
    { _, _, new -> onSelectedChanged(new != null) }
    val selected get() = this.selectedMatch != null
    var deactivated: Boolean by Delegates.observable(deactivated)
    { _, _, new -> onDeactivatedChanged(new) }


    val availableForHighlight: Boolean get() = !deactivated && !selected

    fun intersect(other: MatchPresenter): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }

    inline fun forEachIndexInRanges(action: (Int) -> Unit) = ranges.flatMap { it.asIterable() }.forEach { action(it) }

    companion object {
        private val byPriority = compareByDescending<MatchPresenter> { it.priority }
        val byPriorityAndPosition = byPriority.then(HasRange.byPosition)
    }
}