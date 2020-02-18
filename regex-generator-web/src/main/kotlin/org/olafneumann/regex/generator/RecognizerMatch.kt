package org.olafneumann.regex.generator

data class RecognizerMatch(
    val range: IntRange,
    val inputPart: String,
    val recognizer: Recognizer
) {
    constructor(matchResult: MatchResult, inputPart: String, recognizer: Recognizer): this(matchResult.range, inputPart, recognizer)

    override fun toString() = "[${range.first}+${range.last - range.first}] (${recognizer.name}: ${recognizer.outputPattern}) $inputPart"
}