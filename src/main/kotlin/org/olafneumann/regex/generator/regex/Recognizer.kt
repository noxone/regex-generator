package org.olafneumann.regex.generator.regex

interface Recognizer {
    val name: String
    val description: String?
    val active: Boolean

    fun findMatches(input: String): Collection<RecognizerMatch>
}
