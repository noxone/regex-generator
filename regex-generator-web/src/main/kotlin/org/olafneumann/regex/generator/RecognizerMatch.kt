package org.olafneumann.regex.generator

data class RecognizerMatch(
    val start: Int,
    val end: Int,
    val inputPart: String,
    val recognizer: Recognizer
) {
    override fun toString() = "[$start+${end - start}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}