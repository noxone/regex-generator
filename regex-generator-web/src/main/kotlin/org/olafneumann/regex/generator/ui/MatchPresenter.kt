package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import kotlin.properties.Delegates

class MatchPresenter(
    val recognizerMatches: Collection<RecognizerMatch>,
    selected: Boolean = false,
    deactivated: Boolean = false,
    var onSelectedChanged: (Boolean) -> Unit = {},
    var onDeactivatedChanged: (Boolean) -> Unit = {}
) : HasRange {
    val ranges: List<IntRange>
    init {
        if (recognizerMatches.isEmpty()) {
            throw kotlin.IllegalArgumentException("Empty MatchPresenter is not allowed.")
        }
        val listOfMatches = recognizerMatches.toList()
        for (i in 1 until listOfMatches.size) {
            if (!listOfMatches[0].hasSameRangesAs(listOfMatches[i])) {
                throw kotlin.IllegalArgumentException("All RecognizerMatches in a MatchPresenter must have the same ranges.")
            }
        }
        ranges = listOfMatches[0].ranges
    }

    override val first = ranges.first().first
    override val last = ranges.last().last
    override val length = last - first + 1

    var selected: Boolean by Delegates.observable(selected)
    { _, _, new -> onSelectedChanged(new) }
    var deactivated: Boolean by Delegates.observable(deactivated)
    { _, _, new -> onDeactivatedChanged(new) }

    val availableForHighlight: Boolean get() = !deactivated && !selected

    fun intersect(other: MatchPresenter): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }

    fun forEach(action: (Int) -> Unit) = ranges.flatMap { it.asIterable() }.forEach { action(it) }
}