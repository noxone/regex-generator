package org.olafneumann.regex.generator.regex

data class BracketedRecognizer(
    override val name: String,
    override val outputPatterns: List<String>,
    override val description: String? = null,
    override val active: Boolean = true,
    private val searchPattern: String,
    private val startGroupIndex: Int = 1,
    private val endGroupIndex: Int = 3
) : Recognizer {
    private val searchRegex by lazy { Regex(searchPattern) }

    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .mapNotNull { result ->
                val startGroup = result.groups[startGroupIndex]
                val endGroup = result.groups[endGroupIndex]
                if (startGroup != null && endGroup != null) {
                    val startIndex = getStartGroupIndex(input, startGroup.value)
                    val endIndex = getEndGroupIndex(input, endGroup.value)
                    return@mapNotNull RecognizerMatch(
                        ranges = listOf(
                            IntRange(startIndex, startIndex + startGroup.value.length - 1),
                            IntRange(endIndex, endIndex + endGroup.value.length - 1)
                        ),
                        inputPart = result.value.substring(startIndex, endIndex + endGroup.value.length),
                        recognizer = this
                    )
                }
                return@mapNotNull null
            }.toList()

    private fun getStartGroupIndex(input: String, startGroup: String) = input.indexOf(startGroup)
    private fun getEndGroupIndex(input: String, endGroup: String) = input.lastIndexOf(endGroup)
}