package org.olafneumann.regex.generator.recognizer

interface Recognizer {
    val name: String
    val description: String?
    val isDynamic: Boolean
        get() = false

    fun findMatches(input: String): Collection<RecognizerMatch>
}
