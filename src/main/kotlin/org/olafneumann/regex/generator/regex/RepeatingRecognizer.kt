package org.olafneumann.regex.generator.regex

@Suppress("UnusedPrivateMember")
class RepeatingRecognizer(
    override val name: String,
    override val description: String? = null,
    private val repetitionStart: Recognizer,
    private val repetitionMain: List<Recognizer>
) : Recognizer {
    override fun findMatches(input: String): Collection<RecognizerMatch> =
        emptyList()
}
