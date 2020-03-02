package org.olafneumann.regex.generator.regex

import org.w3c.dom.svg.SVGAnimatedInteger

interface Recognizer {
    val name: String
    val outputPattern: String
    val searchRegex: Regex
    val description: String?
    val active: Boolean
}

data class SimpleRecognizer(
    override val name: String,
    override val outputPattern: String,
    override val description: String? = null,
    val searchPattern: String? = null,
    override val active: Boolean = true
) : Recognizer {
    override val searchRegex by lazy { Regex(searchPattern?.replace("%s", outputPattern) ?: "($outputPattern)") }
}

data class Range(
    val first: Int,
    val last: Int,
    val content: String,
    val type: RangeType
) {
    enum class RangeType {
        Start, Center, End
    }
}

