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
            for (capturingGroup in capturingGroups) {
                val parts = patternPartitionerRegex.findAll(pattern).toList()
                console.log("Pattern: ", pattern, parts, capturingGroup)
                val r1 = if (capturingGroup.range.first != 0) IntRange(start = 0, endInclusive = parts[capturingGroup.range.first - 1].range.last) else null
                val r2 = IntRange(start = parts[capturingGroup.range.first].range.first, endInclusive = parts[capturingGroup.range.last].range.last)
                val r3 = if (capturingGroup.range.last != parts.lastIndex) IntRange(start = parts[capturingGroup.range.last + 1].range.first, endInclusive =  parts.last().range.last) else null

                pattern = "${r1?.let { pattern.substring(it) } ?: ""}(${capturingGroup.name?.let { "?<$it>" } ?: ""}${pattern.substring(r2)})${r3?.let { pattern.substring(it) } ?: ""}"
            }
            return pattern
        }

    private val _capturingGroups = mutableListOf<CapturingGroup>()

    val capturingGroups: List<CapturingGroup>
        get() = _capturingGroups

    fun add(newCapturingGroup: CapturingGroup) {
        for (curCapturingGroup in _capturingGroups) {
            /*if (newCapturingGroup.last < curCapturingGroup.first) {
                // new CG is before current CG
                curCapturingGroup.range = curCapturingGroup.range.plus(2)
            } else if (newCapturingGroup.first > curCapturingGroup.last) {
                // new CG is after current CG
                // do nothing
            } else if (newCapturingGroup.first <= curCapturingGroup.first && newCapturingGroup.last > curCapturingGroup.last) {
                // new CG is around current CG
                //newCapturingGroup.range = newCapturingGroup.range.plus(0, 2)
                //curCapturingGroup.range = curCapturingGroup.range.plus(1)
            } else if (newCapturingGroup.first > curCapturingGroup.first && newCapturingGroup.last < curCapturingGroup.last) {
                // new CG  is inside current CG
                curCapturingGroup.range = curCapturingGroup.range.plus(0, 2)
            } else {
                error("Invalid capturing group position: cur(${curCapturingGroup.toString()}) new(${newCapturingGroup.toString()})")
            }*/
        }
        _capturingGroups.add(newCapturingGroup)
        //_capturingGroups.sortBy { it.last }
    }

    fun remove(capturingGroup: CapturingGroup) {
        _capturingGroups.removeAll { it.id == capturingGroup.id }
    }

    val patternParts: Sequence<MatchResult>
        get() = patternPartitionerRegex.findAll(finalPattern)

    private fun IntRange.plus(amount: Int): IntRange =
        plus(amount, amount)
    private fun IntRange.plus(forFirst: Int, forLast: Int) =
        IntRange(start = start + forFirst, endInclusive = endInclusive + forLast)

    data class CapturingGroup(
        var range: IntRange,
        var name: String?
    ) {
        companion object {
            private var current_id = 0
                get() { return field++ }
        }
        val id = current_id
        val first: Int get() = range.first
        val last: Int get() = range.last
    }
}

