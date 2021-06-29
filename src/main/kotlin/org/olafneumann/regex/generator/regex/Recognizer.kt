package org.olafneumann.regex.generator.regex

interface Recognizer {
    val name: String
    val description: String?

    fun findMatches(input: String): Collection<RecognizerMatch>
}
