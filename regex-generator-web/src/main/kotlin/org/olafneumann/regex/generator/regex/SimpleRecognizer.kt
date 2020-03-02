package org.olafneumann.regex.generator.regex

data class SimpleRecognizer(
    override val name: String,
    override val outputPattern: String,
    override val description: String? = null,
    override val active: Boolean = true,
    private val searchPattern: String? = null,
    private val mainGroupIndex: Int? = 1,
    private val mainGroupName: String? = null
) : Recognizer {
    private val searchRegex by lazy { Regex(searchPattern?.replace("%s", outputPattern) ?: "($outputPattern)") }

    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .map { result ->
                RecognizerMatch(
                    getMainGroupRange(result),
                    getMainGroupValue(result),
                    this
                )
            }.toList()

    private fun getMainGroupValue(result: MatchResult) =
        when {
            mainGroupName != null -> (result.groups as MatchNamedGroupCollection)[mainGroupName]?.value
                ?: throw Exception("Unable to find group '${mainGroupName}'")
            mainGroupIndex != null -> result.groups[mainGroupIndex]?.value
                ?: throw Exception("Unable to find group with index ${mainGroupIndex}.")
            else -> result.value
        }

    // the JS-Regex do not support positions for groups... so we need to use a quite bad work-around (that will not always work)
    private fun getMainGroupRange(result: MatchResult): IntRange {
        val start = result.value.indexOf(getMainGroupValue(result))
        return IntRange(result.range.first + start, result.range.last + start)
    }
}