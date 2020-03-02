package org.olafneumann.regex.generator.regex

data class BracketedRecognizer(
    override val name: String,
    override val outputPattern: String,
    override val description: String?,
    override val active: Boolean
) : Recognizer {
    override fun findMatches(input: String): List<RecognizerMatch> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}