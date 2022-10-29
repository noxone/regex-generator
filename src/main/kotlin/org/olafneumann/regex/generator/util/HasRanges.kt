package org.olafneumann.regex.generator.util

interface HasRanges {
    val ranges: List<IntRange>

    fun hasSameRangesAs(other: HasRanges): Boolean {
        if (ranges.size != other.ranges.size) {
            return false
        }
        return ranges.mapIndexed { index, range -> range == other.ranges[index] }
            .all { it }
    }
}
