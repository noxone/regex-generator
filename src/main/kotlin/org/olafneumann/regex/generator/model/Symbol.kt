package org.olafneumann.regex.generator.model

interface Symbol {
    val index: Int
    val text: String
}

data class RawTextSymbol(
    override val index: Int,
    override val text: String
) : Symbol

data class RawRegexSymbol(
    override val index: Int,
    override val text: String,
    val textSymbols: List<RawTextSymbol>
) : Symbol

data class RegexSymbol(
    override val index: Int,
    override val text: String,
) : Symbol
