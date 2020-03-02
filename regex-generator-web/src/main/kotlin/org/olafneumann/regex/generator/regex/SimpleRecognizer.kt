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

    init {
        if (mainGroupIndex == null && mainGroupName == null) {
            throw IllegalArgumentException(message = "Either mainGroupIndex or mainGroupName must be defined.")
        }
    }

    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .map { result ->
                RecognizerMatch(
                    mainRange = getMainGroupRange(result),
                    inputPart = getMainGroupValue(result),
                    recognizer = this
                )
            }.toList()

    private fun getMainGroupValue(result: MatchResult) =
        when {
            mainGroupName != null -> (result.groups as MatchNamedGroupCollection)[mainGroupName]?.value
                ?: throw Exception("Unable to find group '${mainGroupName}'")
            mainGroupIndex != null -> result.groups[mainGroupIndex]?.value
                ?: throw Exception("Unable to find group with index ${mainGroupIndex}.")
            else -> throw RuntimeException("Neither mainGroupName nor mainGroupIndex defined.")
        }

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