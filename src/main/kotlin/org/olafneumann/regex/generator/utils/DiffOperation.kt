package org.olafneumann.regex.generator.utils

import dev.andrewbailey.diff.DiffOperation
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.utils.RangeAction.Companion.add
import org.olafneumann.regex.generator.utils.RangeAction.Companion.remove

internal val <T> DiffOperation<T>.rangeAction: RangeAction
    get() =
        when (this) {
            is DiffOperation.Add, is DiffOperation.AddAll -> RangeAction(add, range)
            is DiffOperation.Remove, is DiffOperation.RemoveRange -> RangeAction(remove, range)
            else -> throw RegexGeneratorException("Unknown DiffOperation: $this")
        }

internal enum class DiffType {
    Add, Remove
}

internal data class SimpleDiffOperation(
    val type: DiffType,
    val range: IntRange,
)

internal val <T> DiffOperation<T>.simpleDiffOperation: SimpleDiffOperation
    get() =
        when (this) {
            is DiffOperation.Add, is DiffOperation.AddAll -> SimpleDiffOperation(DiffType.Add, range)
            is DiffOperation.Remove, is DiffOperation.RemoveRange -> SimpleDiffOperation(DiffType.Remove, range)
            else -> throw RegexGeneratorException("Unknown DiffOperation: $this")
        }


private val <T> DiffOperation<T>.range: IntRange
    get() =
        when (this) {
            is DiffOperation.Add -> index..index
            is DiffOperation.AddAll -> index until index + items.size
            is DiffOperation.Remove -> index..index
            is DiffOperation.RemoveRange -> startIndex until endIndex
            else -> throw RegexGeneratorException("Unknown DiffOperation: $this")
        }


internal data class RangeAction(
    val action: (IntRange, IntRange) -> List<IntRange>,
    val range: IntRange
) {
    fun applyTo(range: IntRange): List<IntRange> = action(range, this.range)

    companion object {
        val add: (IntRange, IntRange) -> List<IntRange> = { a, b -> a.add(b) }
        val remove: (IntRange, IntRange) -> List<IntRange> = { a, b -> a.remove(b) }
    }
}
