package org.olafneumann.regex.generator.regex

class RepeatingRecognizer(
    override val name: String,
    override val description: String? = null,
    override val active: Boolean = true
) : Recognizer {
    override fun findMatches(input: String): Collection<RecognizerMatch> =
        emptyList()
}