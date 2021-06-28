package org.olafneumann.regex.generator.regex

class EchoRecognizer(
    override val name: String,
    private val pattern: String,
    override val description: String? = null,
    override val active: Boolean = true,
    private val priority: Int = 0,
    private val outputRegexBuilder: String? = null
) : Recognizer {
    override fun findMatches(input: String): List<RecognizerMatch> =
        RegexCache.get(pattern).findAll(input)
            .map { result ->
                RecognizerMatch(
                    patterns = listOf(
                        outputRegexBuilder?.let {
                            it.replace("%1\$s", result.value.replace(RegexCache.get(pattern), "\$1").escapeForRegex())
                        }
                            ?: result.value.escapeForRegex()
                    ),
                    ranges = listOf(result.range),
                    recognizer = this,
                    title = "$name (${result.value})",
                    priority = priority
                )
            }.toList()
}