package org.olafneumann.regex.generator.diff

import dev.andrewbailey.diff.DiffOperation
import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.RegexGeneratorException

internal fun <T> findDifferences(input1: List<T>, input2: List<T>): List<Difference> =
    differenceOf(original = input1, updated = input2, detectMoves = false)
        .operations
        .map { it.toDifference() }

private fun <T> DiffOperation<T>.toDifference(): Difference = when (this) {
    is DiffOperation.Add -> Difference(Difference.Type.Add, index..index)
    is DiffOperation.AddAll -> Difference(Difference.Type.Add, index until index + items.size)
    is DiffOperation.Remove -> Difference(Difference.Type.Remove, index..index)
    is DiffOperation.RemoveRange -> Difference(Difference.Type.Remove, startIndex until endIndex)
    else -> throw RegexGeneratorException("Invalid diff operation: $this")
}

internal data class Difference(
    val type: Type,
    val range: IntRange
) {
    enum class Type {
        Add, Remove
    }
}
