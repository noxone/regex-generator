package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry
import org.olafneumann.regex.generator.util.hasIntersectionWith

class S2PatternRecognition: Model {
    val input: S1UserInput
    val matches: List<RecognizerMatch>
    val selectedMatches: Set<RecognizerMatch>

    constructor(
        input: S1UserInput,
        selectedMatches: Set<RecognizerMatch> = emptySet()
    ) {
        this.input = input
        this.matches = RecognizerRegistry.findMatches(input.output)
        this.selectedMatches = selectedMatches
            .filter { matches.contains(it) }
            .toSet()
    }

    init {
    }

    override val output: String
        get() = RecognizerCombiner
            .combineMatches(
                inputText = input.output,
                selectedMatches = selectedMatches,
                options = RecognizerCombiner.Options())
            .pattern

    fun select(match: RecognizerMatch): S2PatternRecognition {
        // make sure, the selection is valid
        if (!matches.contains(match)) {
            return this
        }

        // make sure, the selection is valid
        val alreadySelectedRanges = selectedMatches.flatMap { it.ranges }
        match.ranges.forEach { rangeOfNewMatch ->
            val hasIntersection = alreadySelectedRanges.firstOrNull { it.hasIntersectionWith(rangeOfNewMatch) } != null
            if (hasIntersection) {
                return this
            }
        }

        return S2PatternRecognition(input = input, selectedMatches = selectedMatches + match)
    }

    fun deselect(match: RecognizerMatch): S2PatternRecognition {
        return S2PatternRecognition(input = input, selectedMatches = selectedMatches - match)
    }

    data class Selection(
        val match: RecognizerMatch
    ) : Model.Change

    data class Deselection(
        val match: RecognizerMatch
    ) : Model.Change
}