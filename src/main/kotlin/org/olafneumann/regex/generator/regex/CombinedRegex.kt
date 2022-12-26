package org.olafneumann.regex.generator.regex

class CombinedRegex(
    val parts: Collection<RecognizerMatchCombiner.RegularExpressionPart>
) {
    val pattern: String = parts.joinToString(separator = "") { it.pattern }
}
