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
    val startRange: IntRange? = null,
    val mainRange: IntRange,
    val endRange: IntRange? = null,
    val inputPart: String,
    val recognizer: Recognizer
) {
    val first: Int = startRange?.first ?: mainRange.first
    val last: Int = endRange?.last ?: mainRange.last
    val length: Int = last - first + 1

    private val totalRange: IntRange = IntRange(start = first, endInclusive = last)

    fun intersect(other: RecognizerMatch): Boolean = totalRange.intersect(other.totalRange).isNotEmpty()

    override fun toString() =
        "[${first}+${last - first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}

