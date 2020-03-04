package org.olafneumann.regex.generator.regex

data class RecognizerMatch(
    val ranges: List<IntRange>,
    val inputPart: String,
    val recognizer: Recognizer
) {
    companion object {
        val comparator = compareBy<RecognizerMatch>({ it.first }, { -it.length })
    }
    init {
        if (ranges.isEmpty()) {
            throw RuntimeException("RecognizerMatch without ranges is not allowed.")
        }
    }

    val first: Int = ranges.map { it.first }.min()!!
    private val last: Int = ranges.map { it.last }.max()!!
    val length: Int = last - first + 1

    fun intersect(other: RecognizerMatch): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }

    override fun toString(): String =
        "[$first+$length] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}


