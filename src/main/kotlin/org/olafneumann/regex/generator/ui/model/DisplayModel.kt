package org.olafneumann.regex.generator.ui.model

import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.model.PatternRecognizerModel
import org.olafneumann.regex.generator.regex.RegexMatchCombiner
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.HtmlView.toCurrentWindowLocation
import org.w3c.dom.url.URL
import kotlin.math.max
import kotlin.math.min

data class DisplayModel(
    val showLoadingIndicator: Boolean,
    val showCookieBanner: Boolean,
    val showCopyButton: Boolean,
    private val patternRecognizerModels: List<PatternRecognizerModel>,
    private val modelPointer: Int,
) {
    private val matchPresenters: Collection<MatchPresenter>
    val rowsOfMatchPresenters: List<List<MatchPresenter>>

    init {
        // Create MatchPresenters
        val matchPresenterBuilders = patternRecognizerModel.recognizerMatches
            .groupBy { it.ranges }
            .map { pair ->
                MatchPresenter.Builder(
                    ranges = pair.key,
                    matches = pair.value,
                    selected = pair.value
                        .firstOrNull { patternRecognizerModel.selectedRecognizerMatches.contains(it) } != null
                )
            }
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

    val patternRecognizerModel: PatternRecognizerModel
        get() = patternRecognizerModels[modelPointer]

    val shareLink: URL
        get() {
            val sampleRegex = encodeURIComponent(patternRecognizerModel.input)
            val flags = patternRecognizerModel.options.flagString
            val selection = patternRecognizerModel.selectedRecognizerMatches
                .map { "${it.first}|${it.recognizer.name}" }
                .joinToString(separator = ",") { encodeURIComponent(it) }
                .ifEmpty { null }

            val searchParameters = mapOf(
                HtmlView.SEARCH_SAMPLE_REGEX to sampleRegex,
                HtmlView.SEARCH_FLAGS to flags,
                HtmlView.SEARCH_SELECTION to selection,
            )
                .filter { it.value != null }
                .map { "${it.key}=${it.value}" }
                .joinToString(prefix = "http://localhost/?", separator = "&")

            return URL(searchParameters).toCurrentWindowLocation()
        }

    val isUndoAvailable: Boolean get() = modelPointer > 0
    val isRedoAvailable: Boolean get() = modelPointer < patternRecognizerModels.lastIndex

    fun performUndo(): DisplayModel =
        copy(modelPointer = max(0, modelPointer - 1))

    fun performRedo(): DisplayModel =
        copy(modelPointer = min(patternRecognizerModels.lastIndex, modelPointer + 1))

    private fun pushNewPatternRecognizerModel(model: PatternRecognizerModel): DisplayModel {
        val listOfOldModels = mutableListOf<PatternRecognizerModel>()
        listOfOldModels.addAll(patternRecognizerModels)
        listOfOldModels.add(model)
        if (listOfOldModels.size > NUMBER_OF_UNDO_SLOTS) {
            listOfOldModels.removeAt(0)
        }
        return copy(patternRecognizerModels = listOfOldModels, modelPointer = listOfOldModels.lastIndex)
    }

    fun setLoadingIndicatorVisible(visible: Boolean): DisplayModel =
        copy(showLoadingIndicator = visible)

    fun setCookieBannerVisible(visible: Boolean): DisplayModel =
        copy(showCookieBanner = visible)

    fun setUserInput(input: String): DisplayModel =
        pushNewPatternRecognizerModel(patternRecognizerModel.setUserInput(input))

    fun setOptions(options: RegexMatchCombiner.Options): DisplayModel =
        pushNewPatternRecognizerModel(patternRecognizerModel.setOptions(options))

    fun select(recognizerMatch: RecognizerMatch): DisplayModel =
        pushNewPatternRecognizerModel(patternRecognizerModel.select(recognizerMatch))

    fun deselect(recognizerMatch: RecognizerMatch): DisplayModel =
        pushNewPatternRecognizerModel(patternRecognizerModel.deselect(recognizerMatch))

    companion object {
        private const val NUMBER_OF_UNDO_SLOTS = 100
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


