package org.olafneumann.regex.generator.utils

import org.olafneumann.regex.generator.diff.Difference
import org.olafneumann.regex.generator.utils.RangeAction.Companion.add
import org.olafneumann.regex.generator.utils.RangeAction.Companion.remove

internal val Difference.rangeAction: RangeAction
    get() =
        when (type) {
            Difference.Type.Add -> RangeAction(add, range)
            Difference.Type.Remove -> RangeAction(remove, range)
        }

@Deprecated("Do not use.")
internal enum class DiffType {
    Add, Remove
}

internal fun Difference.move(index: Int) = ModifiedDifference(type, range).move(index)

@Deprecated("Do not use.")
internal data class ModifiedDifference(
    val type: Difference.Type,
    val range: IntRange,
) {
    fun move(index: Int) = ModifiedDifference(type, range.addPosition(index))
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
