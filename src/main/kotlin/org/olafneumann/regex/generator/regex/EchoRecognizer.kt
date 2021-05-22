package org.olafneumann.regex.generator.regex

class EchoRecognizer(
    override val name: String,
    private val pattern: String,
    override val description: String? = null,
    override val active: Boolean = true,
    private val priority: Int = 0
) : Recognizer {
    override fun findMatches(input: String): List<RecognizerMatch> =
        RegexCache.get(pattern).findAll(input)
            .map { result ->
                RecognizerMatch(
                    patterns = listOf(PatternHelper.escapeForRegex(result.value)),
                    ranges = listOf(result.range),
                    recognizer = this,
                    title = "$name (${result.value})",
                    priority = priority
                )
            }.toList()
}