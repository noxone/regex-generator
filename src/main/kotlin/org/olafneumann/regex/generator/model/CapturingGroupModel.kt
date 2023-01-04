package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.regex.CombinedRegex
import org.olafneumann.regex.generator.utils.IdGenerator
import org.olafneumann.regex.generator.utils.containsAndNotOnEdges

data class CapturingGroupModel(
    val regex: CombinedRegex,
    val capturingGroups: List<CapturingGroup>
) {
    companion object {
        private val VALID_NAME_REGEX = Regex("^[A-Za-z]\\w*$")

        @Suppress("MaxLineLength")
        private val patternSymbolRegex = Regex(
            """(?<complete>\(\?(?:[a-zA-Z]+|[+-]?[0-9]+|&\w+|P=\w+)\))|(?<open>\((?:\?(?::|!|>|=|\||<=|P?<[a-z][a-z0-9]*>|'[a-z][a-z0-9]*'|[-a-z]+:))?)|(?<part>(?:\\.|\[(?:[^\]\\]|\\.)+\]|[^|)])(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<close>\)(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<alt>\|)""",
            options = setOf(RegexOption.IGNORE_CASE)
        )

        fun isCapturingGroupNameValid(name: String?): Boolean =
            name == null || VALID_NAME_REGEX.matches(name)
    }

    val pattern: String by lazy {
        var pattern = regex.pattern

        if (capturingGroups.isNotEmpty()) {
            val bracketPositions = capturingGroups
                .flatMap {
                    listOf(
                        it.openingPosition to it.openingString,
                        it.closingPosition to it.closingString
                    )
                }
                .sortedBy { it.first }

            val stringParts = patternSymbolRegex.findAll(pattern)
                .map { pattern.substring(it.range) }
                .toMutableList()
            for (bp in bracketPositions) {
                stringParts.add(bp.first, bp.second)
            }
            pattern = stringParts.joinToString(separator = "")
        }

        pattern
    }

    private val patternParts = patternSymbolRegex.findAll(pattern)

    internal val rootPatternPartGroup = analyzeRegexGroups()

    private fun analyzeRegexGroups(): PatternPartGroup {
        val rawParts = patternParts
            .mapIndexed { index, matchResult ->
                PatternSymbol(
                    index = index,
                    range = matchResult.range,
                    text = matchResult.value,
                    type = getPatternPartType(matchResult.groups)
                )
            }
            .toList()
        val root = PatternPartGroup()
        var currentLevel = root
        for (part in rawParts) {
            if (part.type == PatternSymbolType.GROUP_START) {
                val newLevel = PatternPartGroup(parent = currentLevel)
                currentLevel.add(newLevel)
                newLevel.add(part)
                currentLevel = newLevel
                if (part.text.startsWith("(?!")) {
                    newLevel.forcedNotSelectable = true
                }
            } else if (part.type == PatternSymbolType.GROUP_END) {
                currentLevel.add(part)
                currentLevel.adjustAlternatives()
                if (currentLevel.parent == null) {
                    error("Unbalanced number of opening and closing brackets.")
                }
                currentLevel = currentLevel.parent!!
            } else if (part.type == PatternSymbolType.CHARACTER) {
                currentLevel.add(part)
                part.selectable = true
            } else if (part.type == PatternSymbolType.ALTERNATIVE || part.type == PatternSymbolType.COMPLETE) {
                currentLevel.add(part)
            } else {
                error("Unknown pattern symbol: ${part.type}")
            }
        }
        return root
    }

    @Suppress("MagicNumber")
    fun addCapturingGroup(
        start: Int,
        endInclusive: Int,
        name: String?,
        quantifier: String? = null
    ): CapturingGroupModel {
        val newCapturingGroups = capturingGroups
            .map { oldCapturingGroup ->
                if (endInclusive < oldCapturingGroup.openingPosition) {
                    // new CG is BEFORE old CG
                    oldCapturingGroup.move(2)
                } else if (start > oldCapturingGroup.closingPosition) {
                    // new CG is BEHIND old CG
                    // do nothing
                    oldCapturingGroup
                } else if (start <= oldCapturingGroup.openingPosition
                    && endInclusive >= oldCapturingGroup.closingPosition
                ) {
                    // new CG is AROUND old CG
                    oldCapturingGroup.move(1)
                } else if (start > oldCapturingGroup.openingPosition
                    && endInclusive < oldCapturingGroup.closingPosition
                ) {
                    // new CG is INSIDE old CG
                    oldCapturingGroup.move(0, 2)
                } else {
                    oldCapturingGroup
                }
            }
            .toMutableList()
        newCapturingGroups.add(CapturingGroup(start, endInclusive + 2, name, quantifier))
        newCapturingGroups.sortBy { it.openingPosition }
        return copy(capturingGroups = newCapturingGroups)
    }

    @Suppress("MagicNumber")
    fun removeCapturingGroup(capturingGroup: CapturingGroup): CapturingGroupModel {
        val newCapturingGroups = capturingGroups
            .mapNotNull { curCapturingGroup ->
                if (capturingGroup.id == curCapturingGroup.id) {
                    null
                } else if (capturingGroup.closingPosition < curCapturingGroup.openingPosition) {
                    // deleted CG was BEFORE current CG
                    curCapturingGroup.move(-2)
                } else if (capturingGroup.openingPosition > curCapturingGroup.closingPosition) {
                    // deleted CG was BEHIND current CG
                    // do nothing
                    curCapturingGroup
                } else if (capturingGroup.openingPosition > curCapturingGroup.openingPosition
                    && capturingGroup.closingPosition < curCapturingGroup.closingPosition
                ) {
                    // deleted CG was INSIDE current CG
                    curCapturingGroup.move(0, -2)
                } else if (capturingGroup.openingPosition < curCapturingGroup.openingPosition
                    && capturingGroup.closingPosition > curCapturingGroup.closingPosition
                ) {
                    // deleted CG was AROUND current CG
                    curCapturingGroup.move(-1)
                } else {
                    curCapturingGroup
                }
            }
        return copy(capturingGroups = newCapturingGroups)
    }

    fun renameCapturingGroup(capturingGroup: CapturingGroup, newName: String?): CapturingGroupModel {
        return changeCapturingGroup(capturingGroup) { it.rename(newName) }
    }

    fun setCapturingGroupQuantifiers(capturingGroup: CapturingGroup, quantifier: String?): CapturingGroupModel {
        return changeCapturingGroup(capturingGroup) { it.copy(quantifier = quantifier) }
    }

    fun setCapturingGroupFlags(capturingGroup: CapturingGroup, flags: Flags): CapturingGroupModel {
        return changeCapturingGroup(capturingGroup) { it.copy(flags = flags) }
    }

    private fun changeCapturingGroup(
        capturingGroup: CapturingGroup,
        action: (CapturingGroup) -> CapturingGroup
    ): CapturingGroupModel {
        val newCapturingGroup = action(capturingGroup)
        val newCapturingGroups = capturingGroups.toMutableList()
        newCapturingGroups.remove(capturingGroup)
        newCapturingGroups.add(newCapturingGroup)
        newCapturingGroups.sortBy { it.openingPosition }
        return copy(capturingGroups = newCapturingGroups)
    }


    internal abstract class PatternPart {
        abstract var parent: PatternPartGroup? // TODO make 'val' again
        abstract val isRoot: Boolean
        abstract val depth: Int

        abstract val firstIndex: Int
        abstract val lastIndex: Int

        abstract val selectable: Boolean

        internal open fun add(part: PatternPart) {
            throw NotImplementedError("'add' not implemented")
        }
    }

    internal class PatternPartGroup(
        override var parent: PatternPartGroup? = null
    ) : PatternPart() {
        override val isRoot: Boolean = parent == null
        override val depth: Int = if (isRoot) 0 else 1 + parent!!.depth

        override val firstIndex: Int
            get() = if (parts.isEmpty()) parent!!.firstIndex else parts.first().firstIndex
        override val lastIndex: Int
            get() = if (parts.isEmpty()) parent!!.lastIndex else parts.last().lastIndex

        private var alternative = false
        val isAlternative: Boolean
            get() = alternative

        var forcedNotSelectable: Boolean = false
        override val selectable: Boolean
            get() = !forcedNotSelectable && (parent?.selectable ?: true)

        val parts: List<PatternPart> get() = mutableParts
        private val mutableParts = mutableListOf<PatternPart>()

        override fun add(part: PatternPart) {
            mutableParts.add(part)
            part.parent = this
        }

        fun adjustAlternatives() {
            val altIndices = parts.mapIndexedNotNull { index, patternPart ->
                if (patternPart is PatternSymbol && patternPart.type == PatternSymbolType.ALTERNATIVE)
                    index
                else
                    null
            }.toMutableList()
            if (altIndices.isEmpty()) {
                return
            }

            val min = 0
            val max = parts.lastIndex
            altIndices.add(0, min)
            altIndices.add(max)
            val oldParts = parts.toList()
            mutableParts.clear()
            for (index in min..max) {
                if (altIndices.contains(index)) {
                    add(oldParts[index])
                    (oldParts[index] as PatternSymbol).selectable = false
                    add(PatternPartGroup(parent = this))
                } else {
                    (mutableParts.last() as PatternPartGroup).add(oldParts[index])
                }
            }
            mutableParts.removeLast()
            alternative = true
        }
    }

    internal data class PatternSymbol(
        val index: Int,
        val range: IntRange,
        val text: String,
        val type: PatternSymbolType
    ) : PatternPart() {
        override var parent: PatternPartGroup? = null
        private var _selectable = false
        override var selectable: Boolean
            set(value) {
                _selectable = value
            }
            get() = parent!!.selectable && _selectable

        override val isRoot: Boolean = false
        override val depth: Int by lazy { 1 + parent!!.depth }

        override val firstIndex: Int = index
        override val lastIndex: Int = index
    }

    internal enum class PatternSymbolType {
        GROUP_START, GROUP_END, COMPLETE, ALTERNATIVE, CHARACTER
    }

    private fun getPatternPartType(groups: MatchGroupCollection): PatternSymbolType {
        return if (groups["part"]?.value != null) {
            PatternSymbolType.CHARACTER
        } else if (groups["open"]?.value != null) {
            PatternSymbolType.GROUP_START
        } else if (groups["close"]?.value != null) {
            PatternSymbolType.GROUP_END
        } else if (groups["alt"]?.value != null) {
            PatternSymbolType.ALTERNATIVE
        } else if (groups["complete"]?.value != null) {
            PatternSymbolType.COMPLETE
        } else {
            throw RegexGeneratorException("Unable to recognize pattern part type from: $groups")
        }
    }

    data class CapturingGroup internal constructor(
        val openingPosition: Int,
        val closingPosition: Int,
        val name: String?,
        val quantifier: String? = null,
        val flags: Flags = Flags()
    ) {
        val id = idGenerator.next

        fun getPublishedClosingPosition(model: CapturingGroupModel): Int =
            closingPosition - 2 - 2 * model.capturingGroups.filter { range.containsAndNotOnEdges(it.range) }.size

        val openingString: String
            get() = "(${name?.let { "?<$it>" } ?: ""}"
        val closingString: String
            get() = ")${quantifier ?: ""}"

        val range: IntRange
            get() = IntRange(openingPosition, closingPosition)

        fun move(distance: Int) = move(distance, distance)
        fun move(distanceOpening: Int, distanceClosing: Int): CapturingGroup =
            copy(
                openingPosition = openingPosition + distanceOpening,
                closingPosition = closingPosition + distanceClosing
            )

        fun rename(newName: String?): CapturingGroup =
            copy(name = newName)

        companion object {
            private val idGenerator = IdGenerator()
        }
    }

    data class Flags(
        val caseSensitive: Boolean? = null,
        val unixLines: Boolean? = null,
        val multiline: Boolean? = null,
        val dotAll: Boolean? = null,
        val unicodeCase: Boolean? = null,
        val comments: Boolean? = null,
    )
}