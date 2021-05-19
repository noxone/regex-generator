package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.util.HasRange

class RecognizerMatch(
    val patterns: List<String>,
    ranges: List<IntRange>,
    val recognizer: Recognizer,
    val title: String,
    val priority: Int = 0
) : HasRange {
    val ranges: List<IntRange>
    override val first: Int
    override val last: Int
    override val length: Int

    init {
        if (ranges.isEmpty()) {
            throw IllegalArgumentException("RecognizerMatch without ranges is not allowed.")
        }
        if (ranges.size != patterns.size) {
            throw IllegalArgumentException("You must provide an equal amount of ranges and patterns")
        }
        this.ranges = ranges.sortedWith(compareBy({ it.first }, { it.last }))
        this.first = this.ranges[0].first
        this.last = this.ranges[this.ranges.size - 1].last
        this.length = last - first + 1
    }

    fun hasSameRangesAs(other: RecognizerMatch): Boolean {
        if (ranges.size != other.ranges.size) {
            return false
        }
        return ranges.mapIndexed { index, range -> range == other.ranges[index] }
            .all { it }
    }

    override fun equals(other: Any?): Boolean =
        other is RecognizerMatch
                && recognizer == other.recognizer
                && hasSameRangesAs(other)

    override fun hashCode(): Int {
        var result = recognizer.hashCode()
        result = 31 * result + patterns.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + ranges.hashCode()
        result = 31 * result + first
        result = 31 * result + last
        result = 31 * result + length
        return result
    }
}


