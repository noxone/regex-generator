package org.olafneumann.regex.generator.regex

data class SimpleRecognizer(
    override val name: String,
    val outputPattern: String,
    override val description: String? = null,
    override val active: Boolean = true,
    private val searchPattern: String? = null,
    private val mainGroupIndex: Int = 1
) : Recognizer {
    override val outputPatterns = listOf(outputPattern)
    private val searchRegex by lazy { Regex(searchPattern?.replace("%s", outputPattern) ?: "($outputPattern)") }


    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .map { result ->
                RecognizerMatch(
                    ranges = listOf(getMainGroupRange(result)),
                    inputPart = getMainGroupValue(result),
                    recognizer = this
                )
            }.toList()

    private fun getMainGroupValue(result: MatchResult) =
        result.groups[mainGroupIndex]?.value ?: throw Exception("Unable to find group with index ${mainGroupIndex}.")

    // the JS-Regex do not support positions for groups... so we need to use a quite bad work-around (that will not always work)
    private fun getMainGroupRange(result: MatchResult): IntRange {
        val mainGroupValue = getMainGroupValue(result)
        val start = result.value.indexOf(mainGroupValue)
        return IntRange(
            start = result.range.first + start,
            endInclusive = result.range.first + start + mainGroupValue.length - 1
        )
    }
}