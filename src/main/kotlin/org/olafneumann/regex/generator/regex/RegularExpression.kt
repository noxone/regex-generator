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

    private val patternAfterPartSelection: String by lazy { parts.joinToString(separator = "") { it.pattern } }

    val finalPattern: String by lazy {
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

            pattern
        }

    private val _capturingGroups = mutableListOf<CapturingGroup>()

    val capturingGroups: List<CapturingGroup>
        get() = _capturingGroups

    @Suppress("MagicNumber")
    fun addCapturingGroup(start: Int, endInclusive: Int, name: String?) {
        _capturingGroups.forEach { oldCapturingGroup ->
            if (endInclusive < oldCapturingGroup.openingPosition) {
                // new CG is BEFORE old CG
                oldCapturingGroup.move(2)
            } else if (start > oldCapturingGroup.closingPosition) {
                // new CG is BEHIND old CG
                // do nothing
            } else if (start <= oldCapturingGroup.openingPosition
                && endInclusive >= oldCapturingGroup.closingPosition) {
                // new CG is AROUND old CG
                oldCapturingGroup.move(1)
            } else if (start > oldCapturingGroup.openingPosition && endInclusive < oldCapturingGroup.closingPosition) {
                // new CG is INSIDE old CG
                oldCapturingGroup.move(0, 2)
            }
        }
        _capturingGroups.add(CapturingGroup(start, endInclusive + 2, name))
        _capturingGroups.sortBy { it.openingPosition }
    }

    @Suppress("MagicNumber")
    fun removeCapturingGroup(id: Int) {
        val indexToDelete = _capturingGroups.indexOfFirst { it.id == id }
        val delCapturingGroup = _capturingGroups.removeAt(index = indexToDelete)

        _capturingGroups.forEach { curCapturingGroup ->
            if (delCapturingGroup.closingPosition < curCapturingGroup.openingPosition) {
                // deleted CG was BEFORE current CG
                curCapturingGroup.move(-2)
            } else if (delCapturingGroup.openingPosition > curCapturingGroup.closingPosition) {
                // deleted CG was BEHIND current CG
                // do nothing
            } else if (delCapturingGroup.openingPosition > curCapturingGroup.openingPosition
                && delCapturingGroup.closingPosition < curCapturingGroup.closingPosition) {
                // deleted CG was INSIDE current CG
                curCapturingGroup.move(0, -2)
            } else if (delCapturingGroup.openingPosition < curCapturingGroup.openingPosition
                && delCapturingGroup.closingPosition > curCapturingGroup.closingPosition) {
                // deleted CG was AROUND current CG
                curCapturingGroup.move(-1)
            }
        }
    }

    val patternParts: Sequence<MatchResult>
        get() = patternPartitionerRegex.findAll(finalPattern)

    data class CapturingGroup internal constructor(
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

        fun move(distance: Int) = move(distance, distance)
        fun move(distanceOpening: Int, distanceClosing: Int) {
            openingPosition += distanceOpening
            closingPosition += distanceClosing
        }

        companion object {
            private var current_id = 0
                get() { return ++field }
        }
    }
}
