package org.olafneumann.regex.generator.diff

import dev.andrewbailey.diff.DiffOperation
import dev.andrewbailey.diff.differenceOf
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.utils.add
import org.olafneumann.regex.generator.utils.addPosition
import org.olafneumann.regex.generator.utils.remove

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
    val range: IntRange,
) {
    val action: RangeAction
        get() = when (type) {
            Type.Add -> RangeAction(add, range)
            Type.Remove -> RangeAction(remove, range)
        }

    internal fun move(index: Int) = ModifiedDifference(type, range).move(index)

    enum class Type {
        Add, Remove
    }

    companion object {
        private val add: (IntRange, IntRange) -> List<IntRange> = { a, b -> a.add(b) }
        private val remove: (IntRange, IntRange) -> List<IntRange> = { a, b -> a.remove(b) }
    }
}

internal data class RangeAction(
    val action: (IntRange, IntRange) -> List<IntRange>,
    val range: IntRange
) {
    fun applyTo(range: IntRange): List<IntRange> = action(range, this.range)
}

internal data class ModifiedDifference(
    val type: Difference.Type,
    val range: IntRange,
) {
    fun move(index: Int) = ModifiedDifference(type, range.addPosition(index))
}
