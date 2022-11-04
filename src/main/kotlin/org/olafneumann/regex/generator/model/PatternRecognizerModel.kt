package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry
import org.olafneumann.regex.generator.util.hasIntersectionWith

class PatternRecognizerModel(
    val input: String,
    val recognizerMatches: List<RecognizerMatch> = RecognizerRegistry.findMatches(input),
    selectedMatches: Collection<RecognizerMatch> = emptySet(),
    val options: RecognizerCombiner.Options
) {
    val id = ID_GENERATOR.currentId
    val selectedRecognizerMatches: Set<RecognizerMatch>

    init {
        this.selectedRecognizerMatches = selectedMatches
            .filter { recognizerMatches.contains(it) }
            .toSet()
    }

    val regularExpression: RecognizerCombiner.RegularExpression
        get() = RecognizerCombiner
            .combineMatches(
                inputText = input,
                selectedMatches = selectedRecognizerMatches,
                options = options
            )

    fun setUserInput(newInput: String): PatternRecognizerModel {
        val newMatches = RecognizerRegistry.findMatches(newInput)

        // check how the input string has changed in regard to the old string
        val inputDiffs = differenceOf(
            original = this.input.toCharArray().toList(),
            updated = newInput.toCharArray().toList(),
            detectMoves = false
        )

        // generate pseudo matches, that resemble possible matches after applying the changes
        val newSelectedMatches = this.selectedRecognizerMatches
            .map { AugmentedRecognizerMatch(original = it) }
            .flatMap { it.applyAll(inputDiffs.operations) }
            // see if the augmented matches are still present in the new list of matches -> the new selection
            .mapNotNull { augmentedMatch -> newMatches.firstOrNull { newMatch -> augmentedMatch.equals(newMatch) } }

        return PatternRecognizerModel(
            input = newInput,
            recognizerMatches = newMatches,
            selectedMatches = newSelectedMatches,
            options = options
        )
    }

    fun select(match: RecognizerMatch): PatternRecognizerModel {
        // make sure, the selection is valid
        if (!recognizerMatches.contains(match)) {
            return this
        }

        // make sure, the selection is valid
        val alreadySelectedRanges = selectedRecognizerMatches.flatMap { it.ranges }
        match.ranges.forEach { rangeOfNewMatch ->
            val hasIntersection = alreadySelectedRanges.firstOrNull { it.hasIntersectionWith(rangeOfNewMatch) } != null
            if (hasIntersection) {
                return this
            }
        }

        return PatternRecognizerModel(
            input = input,
            recognizerMatches = recognizerMatches,
            selectedMatches = selectedRecognizerMatches + match,
            options = RecognizerCombiner.Options()
        )
    }

    fun deselect(match: RecognizerMatch): PatternRecognizerModel {
        return PatternRecognizerModel(
            input = input,
            recognizerMatches = recognizerMatches,
            selectedMatches = selectedRecognizerMatches - match,
            options = RecognizerCombiner.Options()
        )
    }

    fun setOptions(options: RecognizerCombiner.Options) : PatternRecognizerModel {
        return PatternRecognizerModel(
            input = input,
            recognizerMatches = recognizerMatches,
            selectedMatches = selectedRecognizerMatches,
            options = options
        )
    }

    companion object {
        private val ID_GENERATOR = IdGenerator()
    }
}
