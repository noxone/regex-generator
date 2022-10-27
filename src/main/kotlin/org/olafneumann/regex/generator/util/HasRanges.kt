package org.olafneumann.regex.generator.util

import org.olafneumann.regex.generator.regex.RecognizerMatch
import toIndexedString

interface HasRanges {
    val ranges: List<IntRange>

    fun hasSameRangesAs(other: HasRanges): Boolean {
        if (ranges.size != other.ranges.size) {
            return false
        }
        val out = ranges.mapIndexed { index, range -> range == other.ranges[index] }
            .all { it }
        console.log("Range Check", out.toString(), ranges.toIndexedString(), other.ranges.toIndexedString())
        return out
    }
}
