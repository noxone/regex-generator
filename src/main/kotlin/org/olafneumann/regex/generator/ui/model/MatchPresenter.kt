package org.olafneumann.regex.generator.ui.model

import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges

class MatchPresenter private constructor(
    override val ranges: List<IntRange>,
    val recognizerMatches: Collection<RecognizerMatch>,
    val selected: Boolean,
    val deactivated: Boolean
) : HasRange, HasRanges {
    override val first: Int = ranges.minOf { it.first }
    override val last: Int = ranges.maxOf { it.last }
    override val range = IntRange(first, last)
    val priority = recognizerMatches.maxOf { it.priority }

    data class Builder(
        override var ranges: List<IntRange>,
        var matches: Collection<RecognizerMatch>,
        var selected: Boolean,
        var deactivated: Boolean = false
    ) : HasRanges {
        fun deactivated(deactivated: Boolean) { apply { this.deactivated = deactivated } }
        fun build(): MatchPresenter =
            MatchPresenter(
                ranges = ranges,
                recognizerMatches = matches,
                selected = selected,
                deactivated = deactivated
            )
    }



    companion object {
        private val byPriority = compareByDescending<MatchPresenter> { it.priority }
        val byPriorityAndPosition = byPriority.then(HasRange.byPosition)
    }

    @Suppress("MaxLineLength")
    override fun toString(): String {
        return "MatchPresenter(ranges=$ranges, priority=$priority, matches=$recognizerMatches, selected=$selected, deactivated=$deactivated)"
    }
}
