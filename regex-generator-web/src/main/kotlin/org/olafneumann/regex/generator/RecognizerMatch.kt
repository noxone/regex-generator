package org.olafneumann.regex.generator

data class RecognizerMatch(
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}