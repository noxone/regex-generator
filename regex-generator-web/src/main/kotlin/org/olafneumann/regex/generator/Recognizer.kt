package org.olafneumann.regex.generator

data  class Recognizer(
    var name: String,
    var outputPattern: String,
    var description: String? = null,
    var searchPattern: String? = null,
    var active: Boolean = true
) {
    public fun getFinalSearchPattern() = Regex(searchPattern ?: "(?<main>$outputPattern)")
}