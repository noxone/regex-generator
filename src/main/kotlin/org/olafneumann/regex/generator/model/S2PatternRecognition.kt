package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry
import org.olafneumann.regex.generator.util.hasIntersectionWith
import toIndexedString

class S2PatternRecognition : Model {
    val input: String
    val matches: List<RecognizerMatch>
    val selectedMatches: Set<RecognizerMatch>
    val options: RecognizerCombiner.Options

    constructor(
        input: String,
        matches: List<RecognizerMatch> = RecognizerRegistry.findMatches(input),
        selectedMatches: Collection<RecognizerMatch> = emptySet(),
        options: RecognizerCombiner.Options
    ) {
        this.input = input
        this.matches = matches
        this.selectedMatches = selectedMatches
            .filter { matches.contains(it) }
            .toSet()
        this.options = options
    }

    override val output: String
        get() = RecognizerCombiner
            .combineMatches(
                inputText = input,
                selectedMatches = selectedMatches,
                options = options
            )
            .pattern

    fun setUserInput(newInput: String): S2PatternRecognition {
        val newMatches = RecognizerRegistry.findMatches(newInput)
        console.log(newInput)

        // check how the input string has changed in regard to the old string
        val inputDiffs = differenceOf(
            original = this.input.toCharArray().toList(),
            updated = newInput.toCharArray().toList(),
            detectMoves = false
        )
        console.log(inputDiffs.operations.toIndexedString("Diffs: "))

        // generate pseudo matches, that resemble possible matches after applying the changes
        val newSelectedMatches = this.selectedMatches
            .map { AugmentedRecognizerMatch(original = it) }
            .mapNotNull { it.applyAll(inputDiffs.operations) }
            // see if the augmented matches are still present in the new list of matches -> the new selection
            .mapNotNull { augmentedMatch -> newMatches.firstOrNull { newMatch -> augmentedMatch.equals(newMatch) } }

        return S2PatternRecognition(
            input = newInput,
            matches = newMatches,
            selectedMatches = newSelectedMatches,
            options = options
        )
    }

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

        return S2PatternRecognition(
            input = input,
            matches = matches,
            selectedMatches = selectedMatches + match,
            options = RecognizerCombiner.Options()
        )
    }

    fun deselect(match: RecognizerMatch): S2PatternRecognition {
        return S2PatternRecognition(
            input = input,
            matches = matches,
            selectedMatches = selectedMatches - match,
            options = RecognizerCombiner.Options()
        )
    }
}
