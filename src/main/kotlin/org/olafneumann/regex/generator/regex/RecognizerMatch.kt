package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.util.HasRange
import org.olafneumann.regex.generator.util.HasRanges

class RecognizerMatch(
    val patterns: List<String>,
    ranges: List<IntRange>,
    val recognizer: Recognizer,
    val title: String,
    val priority: Int = 0
) : HasRange, HasRanges {
    override val ranges: List<IntRange>
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
        this.last = this.ranges.last().last
        this.length = last - first + 1
    }

    override fun equals(other: Any?): Boolean =
        other is RecognizerMatch
                && recognizer == other.recognizer
                && title == other.title
                && patterns == other.patterns
                && hasSameRangesAs(other)

    override fun hashCode(): Int {
        var result = recognizer.hashCode()
        result = 31 * result + patterns.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + ranges.hashCode()
        return result
    }

    override fun toString(): String {
        return "RecognizerMatch(title='$title', " +
                "position=$range, " +
                "priority=$priority, " +
                "recognizer=${recognizer.name}, " +
                "patterns=$patterns)"
    }

}


