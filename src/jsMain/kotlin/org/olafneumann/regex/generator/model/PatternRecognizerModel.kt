package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.diff.findDifferences
import org.olafneumann.regex.generator.regex.RecognizerMatchCombiner
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import org.olafneumann.regex.generator.recognizer.RecognizerRegistry
import org.olafneumann.regex.generator.regex.CombinedRegex
import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions
import org.olafneumann.regex.generator.utils.hasIntersectionWith

data class PatternRecognizerModel(
    val input: String,
    val recognizerMatches: List<RecognizerMatch> = RecognizerRegistry.findMatches(input),
    val selectedRecognizerMatches: Collection<RecognizerMatch> = emptySet(),
    val recognizerMatchCombinerOptions: RecognizerMatchCombinerOptions,
    val combinedRegex: CombinedRegex = RecognizerMatchCombiner
        .combineMatches(
            inputText = input,
            selectedMatches = selectedRecognizerMatches,
            options = recognizerMatchCombinerOptions
        ),
    val capturingGroupModel: CapturingGroupModel = CapturingGroupModel(combinedRegex, emptyList())
) {
    fun setUserInput(newInput: String): PatternRecognizerModel {
        val newMatches = RecognizerRegistry.findMatches(newInput)

        // check how the input string has changed in regard to the old string
        val differences = findDifferences(
            input1 = this.input.toCharArray().toList(),
            input2 = newInput.toCharArray().toList(),
        )

        // generate pseudo matches, that resemble possible matches after applying the changes
        val newSelectedMatches = this.selectedRecognizerMatches
            .asSequence()
            .map { AugmentedRecognizerMatch(original = it) }
            .flatMap { it.applyAll(differences) }
            // see if the augmented matches are still present in the new list of matches -> the new selection
            .mapNotNull { augmentedMatch -> newMatches.firstOrNull { newMatch -> augmentedMatch.equals(newMatch) } }
            .filter { newMatches.contains(it) }
            .toSet()

        val newRegex = RecognizerMatchCombiner
            .combineMatches(
                inputText = newInput,
                selectedMatches = newSelectedMatches,
                options = recognizerMatchCombinerOptions
            )

        return copy(
            input = newInput,
            recognizerMatches = newMatches,
            selectedRecognizerMatches = newSelectedMatches,
            combinedRegex = newRegex,
            capturingGroupModel = capturingGroupModel.transferToNewRegex(newRegex = newRegex)
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

        val newSelection = selectedRecognizerMatches + match
        val newRegex = RecognizerMatchCombiner
            .combineMatches(inputText = input, selectedMatches = newSelection, options = recognizerMatchCombinerOptions)

        return copy(
            selectedRecognizerMatches = newSelection,
            combinedRegex = newRegex,
            capturingGroupModel = capturingGroupModel.transferToNewRegex(newRegex = newRegex)
        )
    }

    fun deselect(match: RecognizerMatch): PatternRecognizerModel {
        val newSelection = selectedRecognizerMatches - match
        val newRegex = RecognizerMatchCombiner
            .combineMatches(inputText = input, selectedMatches = newSelection, options = recognizerMatchCombinerOptions)

        return copy(
            selectedRecognizerMatches = newSelection,
            combinedRegex = newRegex,
            capturingGroupModel = capturingGroupModel.transferToNewRegex(newRegex = newRegex)
        )
    }

    fun setRecognizerMatchCombinerOptions(options: RecognizerMatchCombinerOptions): PatternRecognizerModel {
        val newRegex = RecognizerMatchCombiner
            .combineMatches(inputText = input, selectedMatches = selectedRecognizerMatches, options = options)
        return copy(
            recognizerMatchCombinerOptions = options,
            combinedRegex = newRegex,
            capturingGroupModel = capturingGroupModel.transferToNewRegex(newRegex = newRegex)
        )
    }

    fun setCapturingGroupModel(capturingGroupModel: CapturingGroupModel): PatternRecognizerModel {
        return copy(capturingGroupModel = capturingGroupModel)
    }

    val finalPattern: String get() = capturingGroupModel.pattern
}
