package org.olafneumann.regex.generator.recognizer

interface Recognizer {
    val name: String
    val description: String?

    fun findMatches(input: String): Collection<RecognizerMatch>
}
