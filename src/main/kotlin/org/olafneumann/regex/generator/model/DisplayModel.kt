package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch

class DisplayModel(
    val showLoadingIndicator: Boolean,
    val showCookieBanner: Boolean,
    val showCopyButton: Boolean,
    val patternRecognitionModel: PatternRecognizerModel,
) {
    private val matchPresenters: Collection<MatchPresenter>
    val rowsOfMatchPresenters: List<List<MatchPresenter>>

    init {
        // Create MatchPresenters
        val matchPresenterBuilders = patternRecognitionModel.recognizerMatches
            .groupBy { it.ranges }
            .map { pair -> MatchPresenter.Builder(
                ranges = pair.key,
                matches = pair.value,
                selected = pair.value
                    .firstOrNull { patternRecognitionModel.selectedRecognizerMatches.contains(it) } != null
            ) }
        val selectedMatchPresenterBuilders = matchPresenterBuilders.filter { it.selected }
        matchPresenterBuilders
            .filter { !it.selected }
            .filter { mpb -> selectedMatchPresenterBuilders.any { it.intersects(mpb) } }
            .forEach { it.deactivated(true) }
        matchPresenters = matchPresenterBuilders
            .map { it.build() }
            .sortedWith(MatchPresenter.byPriorityAndPosition)

        // Distribute to rows for display
        rowsOfMatchPresenters = distributeToRows(matchPresenters)
    }

    val shareLink: String get() {
        // TODO implement share link
        return "SHARE LINK"
    }

    fun setUserInput(input: String): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel.setUserInput(input)
        )
    }

    fun setOptions(options: RecognizerCombiner.Options): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel.setOptions(options)
        )
    }

    fun select(recognizerMatch: RecognizerMatch): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel.select(recognizerMatch)
        )
    }

    fun deselect(recognizerMatch: RecognizerMatch): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel.deselect(recognizerMatch)
        )
    }

    companion object {
        private fun distributeToRows(matches: Collection<MatchPresenter>): List<List<MatchPresenter>> {
            val lines = mutableListOf<MutableList<MatchPresenter>>()
            fun createNextLine(): MutableList<MatchPresenter> {
                val newLine = mutableListOf<MatchPresenter>()
                lines.add(newLine)
                return newLine
            }
            matches
                .forEach { matchPresenter ->
                    val line = lines.firstOrNull { line -> line.last().last < matchPresenter.first } ?: createNextLine()
                    line.add(matchPresenter)
                }
            return lines
        }
    }
}


