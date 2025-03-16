package org.olafneumann.regex.generator.diff

import io.github.petertrr.diffutils.diff
import io.github.petertrr.diffutils.patch.Delta
import io.github.petertrr.diffutils.patch.DeltaType
import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.utils.add
import org.olafneumann.regex.generator.utils.addPosition
import org.olafneumann.regex.generator.utils.remove

internal fun <T> findDifferences(input1: List<T>, input2: List<T>): List<Difference> =
    findDifferencesPeterTrr(input1, input2)

private fun <T> findDifferencesPeterTrr(input1: List<T>, input2: List<T>): List<Difference> =
    diff(source = input1, target = input2)
        .deltas
        .flatMap { it.toDifference() }

private fun <T> Delta<T>.toDifference(): List<Difference> {
    return when (type) {
        DeltaType.INSERT -> listOf(Difference(Difference.Type.Add, target.position..target.last()))
        DeltaType.DELETE -> listOf(Difference(Difference.Type.Remove, source.position..source.last()))

        DeltaType.CHANGE -> listOf(
            Difference(Difference.Type.Remove, source.position..source.last()),
            Difference(Difference.Type.Add, target.position..target.last())
        )

        else -> throw RegexGeneratorException("Invalid delta type: $this")
    }
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
