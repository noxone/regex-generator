package org.olafneumann.regex.generator.regex

class BracketedRecognizer(
    override val name: String,
    private val startPattern: String,
    private val endPattern: String,
    private val centerPatterns: List<CenterPattern>,
    private val startGroupIndex: Int = 1,
    private val endGroupIndex: Int = 3,
    override val description: String? = null,
    override val active: Boolean = true
) : Recognizer {
    override fun findMatches(input: String): Collection<RecognizerMatch> {
        val output = mutableSetOf<RecognizerMatch>()
        var sizeBefore: Int
        do {
            sizeBefore = output.size
            val startIndices = output.map { it.ranges[0].first + 1 }.ifEmpty { listOf(0) }
            startIndices.forEach { startIndex ->
                output.addAll(centerPatterns
                    .associateWith { "(${startPattern})(${it.pattern})(${endPattern})" }
                    .mapValues { RegexCache.get(it.value) }
                    .mapValues { it.value.findAll(input = input, startIndex = startIndex).toList() }
                    .flatMap { it.value.flatMap { result -> createRecognizerMatches(input, it.key, result) } }
                )
            }
        } while (output.size != sizeBefore)
        return output
    }

    private fun createRecognizerMatches(
        input: String,
        centerPattern: CenterPattern,
        result: MatchResult
    ): List<RecognizerMatch> {
        val startGroup = result.groups[startGroupIndex] ?: throw RuntimeException("start group cannot be found")
        val endGroup = result.groups[endGroupIndex] ?: throw RuntimeException("end group cannot be found")
        val startIndex = getStartOfFirstGroup(input, result.range.first, startGroup.value)
        val endIndex = getEndOfLastGroup(input, result.range.last, endGroup.value)
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

        val innerMatch = RecognizerMatch(
            patterns = listOf(centerPattern.pattern),
            ranges = listOf(centerRange),
            recognizer = this,
            title = "$name content (${centerPattern.title.ifEmpty { "center part" }})"
        )

        return mutableListOf(outerMatch, innerMatch)
    }

    private fun getStartOfFirstGroup(input: String, startIndex: Int, group: String) =
        input.indexOf(group, startIndex = startIndex)
    private fun getEndOfLastGroup(input: String, endIndex: Int, group: String) =
        input.lastIndexOf(group, startIndex = endIndex)

    data class CenterPattern(
        val title: String,
        val pattern: String
    )
}
