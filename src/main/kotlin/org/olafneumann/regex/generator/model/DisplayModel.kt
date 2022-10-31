package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.output.UrlGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.ui.HtmlView
import org.w3c.dom.url.URL

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

    val shareLink: URL get() {
        val selection = patternRecognitionModel.selectedRecognizerMatches.map { "${it.first}|${it.recognizer.name}" }
            .joinToString(separator = ",") { encodeURIComponent(it) }
        val searchAddition = mapOf(
            HtmlView.SEARCH_ONLY_PATTERNS to patternRecognitionModel.options.onlyPatterns,
            HtmlView.SEARCH_MATCH_WHOLE_LINE to patternRecognitionModel.options.matchWholeLine,
            HtmlView.SEARCH_SELECTION to selection)
            .map { "${it.key}=${it.value}" }
            .joinToString(separator = "&")

        val shareUrlString = "${urlShareGenerator.generateCode(
            patternRecognitionModel.input, 
            patternRecognitionModel.options).snippet}&${searchAddition}"

        return URL(shareUrlString)
    }

    fun setLoadingIndicatorVisible(visible: Boolean): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = visible,
            showCookieBanner = showCookieBanner,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel
        )
    }

    fun setCookieBannerVisible(visible: Boolean): DisplayModel {
        return DisplayModel(
            showLoadingIndicator = showLoadingIndicator,
            showCookieBanner = visible,
            showCopyButton = showCopyButton,
            patternRecognitionModel = patternRecognitionModel
        )
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

        private val urlShareGenerator = UrlGenerator(
            linkName = "ShareLink",
            urlTemplate = "https://regex-generator.olafneumann.org/?sampleText=%1\$s&flags=%2\$s"
        )
    }
}


