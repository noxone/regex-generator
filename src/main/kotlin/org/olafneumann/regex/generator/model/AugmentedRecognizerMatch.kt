package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.RegexGeneratorException
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
        return if (ranges.size == 1) {
            val possibleRanges = rangeAction.applyTo(ranges[0])
            possibleRanges.map { AugmentedRecognizerMatch(original = original, ranges = listOf(it)) }
        } else {
            val rangeAlternatives = ranges.map { rangeAction.applyTo(it) }
            crossProduct(rangeAlternatives)
                .map { AugmentedRecognizerMatch(original = original, ranges = it) }
        }
    }

    private fun <T> crossProduct(lists: List<List<T>>): List<List<T>> {
        return if (lists.size == 2) {
            crossProduct(lists[0], lists[1])
        } else if (lists.size > 2) {
            val left = lists[0]
            val right = crossProduct(lists.subList(1, lists.lastIndex))
            left.flatMap { leftItem -> right.map { rightItem -> leftItem to rightItem } }
                .map { concat(it.first, it.second) }
        } else {
            throw RegexGeneratorException("Invalid list of lists: $lists")
        }
    }

    private fun <T> crossProduct(a: List<T>, b: List<T>): List<List<T>> {
        return a.flatMap { at -> b.map { bt -> at to bt } }
            .map { listOf(it.first, it.second) }
    }

    private fun <T> concat(item: T, list: List<T>): List<T> {
        val out = mutableListOf(item)
        out.addAll(list)
        return out
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
