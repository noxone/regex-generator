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
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}

data class Range(
    val first: Int,
    val last: Int,
    val content: String,
    val type: RangeType
) {
    enum class RangeType {
        Start, Center, End
    }
}

