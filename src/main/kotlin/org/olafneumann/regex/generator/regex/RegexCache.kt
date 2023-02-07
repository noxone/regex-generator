package org.olafneumann.regex.generator.regex

object RegexCache {
    private val patternToRegex = mutableMapOf<String, Regex>()

    fun get(pattern: String): Regex =
        patternToRegex.getOrPut(pattern) {
            console.info("Creating regex for: ", pattern)
            Regex(pattern)
        }
}
