package org.olafneumann.regex.generator.regex

class RecognizerMatch(
    ranges: List<IntRange>,
    val inputPart: String,
    val recognizer: Recognizer
) {
    val ranges: List<IntRange>
    val first: Int
    private val last: Int
    val length: Int

    init {
        if (ranges.isEmpty()) {
            throw RuntimeException("RecognizerMatch without ranges is not allowed.")
        }
        this.ranges = ranges.sortedWith(compareBy({ it.first }, { it.last }))
        this.first = this.ranges[0].first
        this.last = this.ranges[this.ranges.size - 1].last
        this.length = last - first + 1
    }

    fun intersect(other: RecognizerMatch): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }

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

    override fun toString(): String =
        "[$first+$length] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"

    companion object {
        val comparator = compareBy<RecognizerMatch>({ it.first }, { -it.length })
    }
}


