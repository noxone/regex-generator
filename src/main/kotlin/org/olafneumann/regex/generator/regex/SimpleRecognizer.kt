package org.olafneumann.regex.generator.regex

class SimpleRecognizer(
    override val name: String,
    private val outputPattern: String,
    override val description: String? = null,
    private val searchPattern: String? = null,
    private val mainGroupIndex: Int = 1
) : Recognizer {
    private val searchRegex = RegexCache.get(searchPattern?.replace("%s", outputPattern) ?: "(${outputPattern})")


    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .map { result ->
                RecognizerMatch(
                    patterns = listOf(outputPattern),
                    ranges = listOf(getMainGroupRange(result)),
                    recognizer = this,
                    title = name
                )
            }.toList()

    private fun getMainGroupValue(result: MatchResult) =
        result.groups[mainGroupIndex]?.value
            ?: throw RecognizerException("Unable to find group with index ${mainGroupIndex}.")

    // the JS-Regex do not support positions for groups...
    // so we need to use a quite bad work-around (that will not always work)
    private fun getMainGroupRange(result: MatchResult): IntRange {
        val mainGroupValue = getMainGroupValue(result)
        val start = result.value.indexOf(mainGroupValue)
        return IntRange(
            start = result.range.first + start,
            endInclusive = result.range.first + start + mainGroupValue.length - 1
        )
    }

    override fun equals(other: Any?) = other is SimpleRecognizer
            && this.name == other.name
            && this.outputPattern == other.outputPattern
            && this.searchPattern == other.searchPattern

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + outputPattern.hashCode()
        result = 31 * result + (searchPattern?.hashCode() ?: 0)
        return result
    }
}
