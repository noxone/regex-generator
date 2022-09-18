package org.olafneumann.regex.generator.regex


data class RegularExpression(
    val parts: Collection<RecognizerCombiner.RegularExpressionPart>
) {
    companion object {
        @Suppress("MaxLineLength")
        private val patternPartitionerRegex = Regex(
            """(?<complete>\(\?(?:[a-zA-Z]+|[+-]?[0-9]+|&\w+|P=\w+)\))|(?<open>\((?:\?(?::|!|>|=|\||<=|P?<[a-z][a-z0-9]*>|'[a-z][a-z0-9]*'|[-a-z]+:))?)|(?<part>(?:\\.|\[(?:[^\]\\]|\\.)+\]|[^|)])(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<close>\)(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<alt>\|)""",
            options = setOf(RegexOption.IGNORE_CASE)
        )
    }

    val patternAfterPartSelection: String
        get() = parts.joinToString(separator = "") { it.pattern }

    val finalPattern: String
        get() {
            var pattern = patternAfterPartSelection

            if (capturingGroups.isNotEmpty()) {
                val bracketPositions = _capturingGroups
                    .flatMap { listOf(
                        it.openingPosition to it.openingString,
                        it.closingPosition to it.closingString
                    ) }
                    .sortedBy { it.first }

                val stringParts = patternPartitionerRegex.findAll(pattern)
                    .map { pattern.substring(it.range) }
                    .toMutableList()
                for (bp in bracketPositions) {
                    stringParts.add(bp.first, bp.second)
                }
                pattern = stringParts.joinToString(separator = "")
            }

            return pattern
        }

    private val _capturingGroups = mutableListOf<CapturingGroup>()

    val capturingGroups: List<CapturingGroup>
        get() = _capturingGroups

    fun add(newCapturingGroup: CapturingGroup) {
        _capturingGroups.add(newCapturingGroup)
    }

    fun remove(capturingGroup: CapturingGroup) {
        // console.log("Current list: ", _capturingGroups, "Delete: ", capturingGroup)

        val indexToDelete = _capturingGroups.indexOfFirst { it.id == capturingGroup.id }
        val oldCapturingGroup = _capturingGroups.removeAt(index = indexToDelete)
    }

    val patternParts: Sequence<MatchResult>
        get() = patternPartitionerRegex.findAll(finalPattern)

    private fun IntRange.minus(amount: Int): IntRange = plus(-amount)
    private fun IntRange.minus(forFirst: Int, forLast: Int) = plus(-forFirst, -forLast)
    private fun IntRange.plus(amount: Int): IntRange =
        plus(amount, amount)
    private fun IntRange.plus(forFirst: Int, forLast: Int) =
        IntRange(start = start + forFirst, endInclusive = endInclusive + forLast)

    data class CapturingGroup(
        var openingPosition: Int,
        var closingPosition: Int,
        var name: String?
    ) {
        val id = current_id

        val openingString: String
            get() = "(${name?.let { "?<$it>" } ?: ""}"
        val closingString: String
            get() = ")"

        val range: IntRange
            get() = IntRange(openingPosition, closingPosition)

        companion object {
            private var current_id = 0
                get() { return field++ }
        }
    }
}

