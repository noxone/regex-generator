package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import dev.andrewbailey.diff.DiffResult
import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry

class S2PatternRecognition(
    val input: S1UserInput,
    previousMatches: List<RecognizerMatch> = emptyList(),
    inputChange: S1UserInput.Change? = null
) : Model {
    val matches: List<RecognizerMatch>
    val diffs: DiffResult<RecognizerMatch>

    init {
        val matches = RecognizerRegistry.findMatches(input.output)

        diffs = differenceOf(previousMatches, matches)
        val augmentedMatches = previousMatches.map { AugmentedRecognizerMatch(match = it) }
            .forEach { match -> inputChange
                ?.changes
                ?.forEach { change -> change.applyOn(match) } }
        
        this.matches = matches
    }

    override val output: String
        get() = input.output

    fun setUserInput(modelWithDelta: ModelWithDelta<S1UserInput, S1UserInput.Change>): S2PatternRecognition =
        S2PatternRecognition(input = modelWithDelta.model, previousMatches = matches, inputChange = modelWithDelta.change)

    data class Selection(
        val match: RecognizerMatch
    ) : Model.Change

    data class Deselection(
        val match: RecognizerMatch
    ) : Model.Change

    private fun DiffOperation<Char>.applyOn(match: AugmentedRecognizerMatch) {
        when (this) {
            is DiffOperation.Add -> console.log("add")
            is DiffOperation.AddAll -> console.log("addAll")
            is DiffOperation.Remove -> console.log("remove")
            is DiffOperation.RemoveRange -> console.log("removeRange")
            is DiffOperation.Move -> console.log("move")
            is DiffOperation.MoveRange -> console.log("moveRange")
            else -> console.log("ELSE")
        }
    }
}