package org.olafneumann.regex.generator.regex


data class RegularExpression(
    val parts: Collection<RecognizerCombiner.RegularExpressionPart>
) {
    val patternAfterPartSelection: String
        get() = parts.joinToString(separator = "") { it.pattern }

    val finalPattern: String
        get() = patternAfterPartSelection // TODO add capturing groups

    private val _capturingGroups = mutableListOf<CapturingGroup>()

    val capturingGroups: List<CapturingGroup>
        get() = _capturingGroups

    fun add(capturingGroup: CapturingGroup) {
        _capturingGroups.add(capturingGroup)
        _capturingGroups.sortBy { it.range.first }
    }

    data class CapturingGroup(
        val range: IntRange,
        var name: String?
    )
}

