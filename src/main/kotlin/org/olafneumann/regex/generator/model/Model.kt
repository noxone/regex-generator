package org.olafneumann.regex.generator.model

data class Model(
    val userInput: String,
    val rawTextSymbols: List<RawTextSymbol>,
    val rawRegexSymbols: List<RawRegexSymbol>
) {
}