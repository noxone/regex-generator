package org.olafneumann.regex.generator.regex

interface Recognizer {
    val name: String
    val outputPattern: String
    val description: String?
    val active: Boolean

    fun findMatches(input: String): List<RecognizerMatch>

    companion object {
        fun recognize(input: String, config: Configuration = Configuration.default) =
            config.recognizers
                .flatMap { it.findMatches(input) }
    }
}

data class RecognizerMatch(
    val ranges: List<IntRange>,
    val inputPart: String,
    val recognizer: Recognizer
) {
    init {
        if (ranges.isEmpty()) {
            throw RuntimeException("RecognizerMatch without ranges is not allowed.")
        }
    }

    val first: Int by lazy { ranges.map { it.first }.min()!! }
    val last: Int by lazy { ranges.map { it.last }.max()!! }
    val length: Int = last - first + 1

    fun intersect(other: RecognizerMatch): Boolean =
        ranges.flatMap { thisRange -> other.ranges.map { otherRange -> thisRange to otherRange } }
            .any { it.first.intersect(it.second).isNotEmpty() }

    override fun toString(): String =
        "[$first+$length] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}

