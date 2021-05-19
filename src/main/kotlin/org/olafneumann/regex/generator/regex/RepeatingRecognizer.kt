package org.olafneumann.regex.generator.regex

class RepeatingRecognizer(
    override val name: String,
    override val description: String? = null,
    override val active: Boolean = true,
    private val repetitionStart: Recognizer,
    private val repetitionMain: List<Recognizer>
) : Recognizer {
    override fun findMatches(input: String): Collection<RecognizerMatch> =
        emptyList()
}