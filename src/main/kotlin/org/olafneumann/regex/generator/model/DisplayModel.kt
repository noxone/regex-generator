package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.ui.MatchPresenter
import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges

class DisplayModel(
    val showLoadingIndicator: Boolean,
    val showCookieBanner: Boolean,
    val showCopyButton: Boolean,
    val patternRecognitionModel: PatternRecognizerModel,
) {
    private val matchPresenters: Collection<MatchPresenter2>
    val rowsOfMatchPresenters: List<List<MatchPresenter2>>

    init {
        // Create MatchPresenters
        val matchPresenterBuilders = patternRecognitionModel.matches
            .groupBy { it.ranges }
            .map { pair -> MatchPresenter2.Builder(
                ranges = pair.key,
                matches = pair.value,
                selected = pair.value.firstOrNull { patternRecognitionModel.selectedMatches.contains(it) } != null
            ) }
        val selectedMatchPresenterBuilders = matchPresenterBuilders.filter { it.selected }
        matchPresenterBuilders
            .filter { mpb -> selectedMatchPresenterBuilders.any { it.intersects(mpb) } }
            .forEach { it.deactivated(true) }
        matchPresenters = matchPresenterBuilders
            .map { it.build() }
            .sortedWith(MatchPresenter2.byPriorityAndPosition)

        // Distribute to rows for display
        rowsOfMatchPresenters = distributeToRows(matchPresenters)
    }

    val shareLink: String get() = TODO("Implement shareLink")

    fun newPatternRecognitionModel(patternRecognitionModel: PatternRecognizerModel): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel
        )
    }

    companion object {
        private fun distributeToRows(matches: Collection<MatchPresenter2>): List<List<MatchPresenter2>> {
            val lines = mutableListOf<MutableList<MatchPresenter2>>()
            fun createNextLine(): MutableList<MatchPresenter2> {
                val newLine = mutableListOf<MatchPresenter2>()
                lines.add(newLine)
                return newLine
            }
            matches
                .forEach { presenter ->
                    lines
                        .firstOrNull { line -> line.last().last < presenter.first } ?: createNextLine()
                        .add(presenter)
                }
            return lines
        }
    }
}

class MatchPresenter2 private constructor(
    override val ranges: List<IntRange>,
    val matches: Collection<RecognizerMatch>,
    val selected: Boolean,
    val deactivated: Boolean
) : HasRange, HasRanges {
    override val first: Int = ranges.minOf { it.first }
    override val last: Int = ranges.maxOf { it.last }
    override val range = IntRange(first, last)
    val priority = matches.maxOf { it.priority }

    data class Builder(
        override var ranges: List<IntRange>,
        var matches: Collection<RecognizerMatch>,
        var selected: Boolean,
        var deactivated: Boolean = false
    ) : HasRanges {
        fun deactivated(deactivated: Boolean) { apply { this.deactivated = deactivated } }
        fun build(): MatchPresenter2 =
            MatchPresenter2(
                ranges = ranges,
                matches = matches,
                selected = selected,
                deactivated = deactivated
            )
    }

    companion object {
        private val byPriority = compareByDescending<MatchPresenter2> { it.priority }
        val byPriorityAndPosition = byPriority.then(HasRange.byPosition)
    }
}
