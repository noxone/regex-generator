package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.regex.RecognizerMatch
import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges
import org.olafneumann.regex.generator.util.rangeAction

internal class AugmentedRecognizerMatch(
    val original: RecognizerMatch,
    override val ranges: List<IntRange> = original.ranges
) : HasRange, HasRanges {
    override val first: Int
        get() = this.ranges[0].first
    override val last: Int
        get() = this.ranges.last().last

    fun <T> applyAll(diffOperations: List<DiffOperation<T>>?): List<AugmentedRecognizerMatch> {
        var out: List<AugmentedRecognizerMatch> = listOf(this)
        diffOperations?.forEach { diffOp -> out = out.flatMap { it.apply(diffOp) } }
        return out
    }

    private fun <T> apply(diffOperation: DiffOperation<T>): List<AugmentedRecognizerMatch> {
        if (ranges.isEmpty()) {
            return emptyList()
        }

        val rangeAction = diffOperation.rangeAction
        if (ranges.size == 1) {
            val possibleRanges = rangeAction.applyTo(ranges[0])
            return possibleRanges.map { AugmentedRecognizerMatch(original = original, ranges = listOf(it)) }
        } else if (ranges.size == 2) {
            val rangeAlternatives = ranges.map { rangeAction.applyTo(it) }
            return crossProduct(rangeAlternatives[0], rangeAlternatives[1])
                .map { AugmentedRecognizerMatch(original = original, ranges = it) }
        } else {
            val rangesAfterAction = ranges.map { rangeAction.applyTo(it) }
            if (rangesAfterAction.any { it.isEmpty() }) {
                return emptyList()
            }

            val maxRangeCount = rangesAfterAction.maxOfOrNull { it.size }!!
            return rangesAfterAction
                .map { listOfRanges -> (1..maxRangeCount).map { listOfRanges[it % listOfRanges.size] } }
                .map { AugmentedRecognizerMatch(original = original, ranges = it) }
        }
    }

    private fun <T> crossProduct(a: List<T>, b: List<T>): List<List<T>> {
        return a.flatMap { at -> b.map { bt -> at to bt } }
            .map { listOf(it.first, it.second) }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            is AugmentedRecognizerMatch ->
                original.recognizer == other.original.recognizer
                        && original.title == other.original.title
                        && original.patterns == other.original.patterns
                        && this.hasSameRangesAs(other)

            is RecognizerMatch ->
                original.recognizer == other.recognizer
                        && original.title == other.title
                        && original.patterns == other.patterns
                        && this.hasSameRangesAs(other)

            else -> false
        }
    }


    override fun hashCode(): Int {
        var result = original.recognizer.hashCode()
        result = 31 * result + original.patterns.hashCode()
        result = 31 * result + original.title.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }

    override fun toString(): String {
        return "AugmentedRecognizerMatch(title=${original.title}, " +
                "position=$range, " +
                "priority=${original.priority}, " +
                "recognizer=${original.recognizer.name}, " +
                "patterns=${original.patterns})"
    }
}
