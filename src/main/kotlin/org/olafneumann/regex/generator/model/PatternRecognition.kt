package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffResult
import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.regex.RecognizerRegistry

class PatternRecognition(
    val input: UserInput,
    previousMatches: List<RecognizerMatch> = emptyList(),
    change: UserInput.Change? = null
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

    data class Selection(
        val match: RecognizerMatch
    ) : Model.Change

    data class Deselection(
        val match: RecognizerMatch
    ) : Model.Change

    private class RMShadow(
        val originalMatch: RecognizerMatch
    ) {
        val first: Int get() = originalMatch.first
        val length: Int get() = originalMatch.length

        override fun equals(other: Any?): Boolean {
            console.log("equals", other.toString())
            return originalMatch == other
        }
    }
}