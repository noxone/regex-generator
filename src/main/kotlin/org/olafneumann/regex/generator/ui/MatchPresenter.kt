package org.olafneumann.regex.generator.ui

import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges
import kotlin.properties.Delegates

class MatchPresenter(
    val recognizerMatches: List<RecognizerMatch>,
    selectedMatch: RecognizerMatch? = null,
    deactivated: Boolean = false,
    var onSelectedChanged: (Boolean) -> Unit = {},
    var onDeactivatedChanged: (Boolean) -> Unit = {}
) : HasRange, HasRanges {
    override val ranges: List<IntRange>

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

    override val first = ranges.minOf { it.first }
    override val last = ranges.maxOf { it.last }
    override val range = IntRange(first, last)

    val priority = recognizerMatches.maxOf { it.priority }

    var selectedMatch: RecognizerMatch? by Delegates.observable(selectedMatch)
        { _, _, new -> onSelectedChanged(new != null) }
    val selected get() = this.selectedMatch != null
    var deactivated: Boolean by Delegates.observable(deactivated)
        { _, _, new -> onDeactivatedChanged(new) }


    val availableForHighlight: Boolean get() = !deactivated && !selected

    inline fun forEachIndexInRanges(action: (Int) -> Unit) = ranges.flatMap { it.asIterable() }.forEach { action(it) }

    companion object {
        private val byPriority = compareByDescending<MatchPresenter> { it.priority }
        val byPriorityAndPosition = byPriority.then(HasRange.byPosition)
    }
}
