package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffResult
import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry

class S2PatternRecognition(
    val input: S1UserInput,
    previousMatches: List<RecognizerMatch> = emptyList(),
    change: S1UserInput.Change? = null
) : Model {
    val matches: List<RecognizerMatch>
    val diffs: DiffResult<RecognizerMatch>

    init {
        val matches = RecognizerRegistry.findMatches(input.output)

        diffs = differenceOf(previousMatches, matches)


        this.matches = matches
    }

    override val output: String
        get() = input.output

    fun setUserInput(modelWithDelta: ModelWithDelta<S1UserInput, S1UserInput.Change>): S2PatternRecognition =
        S2PatternRecognition(input = modelWithDelta.model, previousMatches = matches, change = modelWithDelta.change)

    data class Selection(
        val match: RecognizerMatch
    ) : Model.Change

    data class Deselection(
        val match: RecognizerMatch
    ) : Model.Change
}