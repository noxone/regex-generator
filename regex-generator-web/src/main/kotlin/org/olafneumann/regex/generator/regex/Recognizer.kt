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
    val startRange: Range? = null,
    val mainRange: Range,
    val endRange: Range? = null,
    val inputPart: String,
    val recognizer: Recognizer
) {
    val first: Int = startRange?.first ?: mainRange.first
    val last: Int = endRange?.last ?: mainRange.last

    override fun toString() =
        "[${first}+${last - first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}

data class Range(
    val first: Int,
    val last: Int,
    val content: String? = null
) {
}

