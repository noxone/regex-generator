package org.olafneumann.regex.generator.util

import org.olafneumann.regex.generator.ui.MatchPresenter

interface HasRanges {
    val ranges: List<IntRange>

    fun hasSameRangesAs(other: HasRanges): Boolean {
        if (ranges.size != other.ranges.size) {
            return false
        }
        return ranges.mapIndexed { index, range -> range == other.ranges[index] }
            .all { it }
    }

    fun intersects(other: HasRanges): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }
}
