package org.olafneumann.regex.generator.regex

class BracketedRecognizer(
    override val name: String,
    private val startPattern: String,
    private val centerPatterns: List<CenterPattern>,
    private val endPattern: String,
    private val searchPattern: String,
    private val startGroupIndex: Int = 1,
    private val endGroupIndex: Int = 3,
    override val description: String? = null,
    override val active: Boolean = true
) : Recognizer {
    private val searchRegex by lazy { Regex(searchPattern) }

    override fun findMatches(input: String): List<RecognizerMatch> =
        searchRegex.findAll(input)
            .toList()
            .flatMap { handleResult(input, it) }

    // TODO allow different center patterns
    private fun handleResult(input: String, result: MatchResult): List<RecognizerMatch> {
        val startGroup = result.groups[startGroupIndex] ?: throw RuntimeException("start group cannot be found")
        val endGroup = result.groups[endGroupIndex] ?: throw RuntimeException("end group cannot be found")
        val startIndex = getStartOfFirstGroup(input, startGroup.value)
        val endIndex = getEndOfLastGroup(input, endGroup.value)
        val startRange = IntRange(startIndex, startIndex + startGroup.value.length - 1)
        val centerRange = IntRange(startIndex + startGroup.value.length, endIndex - 1)
        val endRange = IntRange(endIndex, endIndex + endGroup.value.length - 1)

        // first the match for the enclosing stuff
        val outerMatch = RecognizerMatch(
            patterns = listOf(startPattern, endPattern),
            ranges = listOf(startRange, endRange),
            recognizer = this,
            title = name
        )
        // then the match for the inner stuff
        val innerMatches = centerPatterns.map {centerPattern ->
                RecognizerMatch(
                    patterns = listOf(centerPattern.pattern),
                    ranges = listOf(centerRange),
                    recognizer = this,
                    title = "$name (${centerPattern.title.ifEmpty { "center part" }})"
                )
            }

        // combine all matches
        val mutableList = mutableListOf(outerMatch)
        mutableList.addAll(innerMatches)
        return mutableList
    }

    private fun getStartOfFirstGroup(input: String, group: String) = input.indexOf(group)
    private fun getEndOfLastGroup(input: String, group: String) = input.lastIndexOf(group)

    data class CenterPattern(
        val title: String,
        val pattern: String
    )
}