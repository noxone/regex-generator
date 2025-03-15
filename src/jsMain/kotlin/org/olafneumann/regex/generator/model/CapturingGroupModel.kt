package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.RegexGeneratorException
import org.olafneumann.regex.generator.diff.Difference
import org.olafneumann.regex.generator.diff.findDifferences
import org.olafneumann.regex.generator.regex.CombinedRegex
import org.olafneumann.regex.generator.utils.IdGenerator
import org.olafneumann.regex.generator.utils.containsAndNotOnEdges
import org.olafneumann.regex.generator.utils.length
import org.olafneumann.regex.generator.utils.toIndexedString

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

    private val positionedBrackets: List<PositionedBracket> = capturingGroups
        .flatMap { it.positionedBrackets }
        .sortedBy { it.position }

    val pattern: String = run {
        var pattern = regex.pattern
        if (capturingGroups.isNotEmpty()) {

            val stringParts = patternSymbolRegex.findAll(pattern)
                .map { pattern.substring(it.range) }
                .toMutableList()
            for (positionedBracket in positionedBrackets) {
                stringParts.add(positionedBracket.position, positionedBracket.content)
            }
            pattern = stringParts.joinToString(separator = "")
        }
        pattern
    }

    internal val rootPatternPartGroup = analyzeRegexGroups()

    private fun getPatternSymbols(input: String): Sequence<PatternSymbol> =
        patternSymbolRegex.findAll(input)
            .mapIndexed { index, matchResult ->
                PatternSymbol(
                    index = index,
                    range = matchResult.range,
                    text = matchResult.value,
                    type = getPatternPartType(matchResult.groups)
                )
            }

    private fun analyzeRegexGroups(): PatternPartGroup {
        val rawParts = getPatternSymbols(pattern)
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
                when {
                    endInclusive < oldCapturingGroup.openingPosition ->
                        // new CG is BEFORE old CG
                        oldCapturingGroup.move(2)

                    start > oldCapturingGroup.closingPosition ->
                        // new CG is BEHIND old CG
                        // do nothing
                        oldCapturingGroup

                    start <= oldCapturingGroup.openingPosition
                            && endInclusive >= oldCapturingGroup.closingPosition ->
                        // new CG is AROUND old CG
                        oldCapturingGroup.move(1)

                    start > oldCapturingGroup.openingPosition
                            && endInclusive < oldCapturingGroup.closingPosition ->
                        // new CG is INSIDE old CG
                        oldCapturingGroup.move(0, 2)

                    else ->
                        oldCapturingGroup
                }
            }
            .toMutableList()
        newCapturingGroups.add(CapturingGroup(start, endInclusive + 2, name, quantifier))
        newCapturingGroups.sortBy { it.openingPosition }
        return copy(capturingGroups = newCapturingGroups)
    }

    fun removeCapturingGroup(capturingGroup: CapturingGroup): CapturingGroupModel {
        return copy(capturingGroups = removeCapturingGroupInternal(capturingGroups, capturingGroup))
    }

    @Suppress("MagicNumber")
    private fun removeCapturingGroupInternal(
        capturingGroups: List<CapturingGroup>,
        capturingGroup: CapturingGroup
    ): List<CapturingGroup> {
        return capturingGroups
            .mapNotNull { curCapturingGroup ->
                when {
                    capturingGroup.id == curCapturingGroup.id ->
                        null

                    capturingGroup.closingPosition < curCapturingGroup.openingPosition ->
                        // deleted CG was BEFORE current CG
                        curCapturingGroup.move(-2)

                    capturingGroup.openingPosition > curCapturingGroup.closingPosition ->
                        // deleted CG was BEHIND current CG
                        // do nothing
                        curCapturingGroup

                    capturingGroup.openingPosition > curCapturingGroup.openingPosition
                            && capturingGroup.closingPosition < curCapturingGroup.closingPosition ->
                        // deleted CG was INSIDE current CG
                        curCapturingGroup.move(0, -2)

                    capturingGroup.openingPosition < curCapturingGroup.openingPosition
                            && capturingGroup.closingPosition > curCapturingGroup.closingPosition ->
                        // deleted CG was AROUND current CG
                        curCapturingGroup.move(-1)

                    else ->
                        curCapturingGroup
                }
            }
    }

    private fun getMovedPatternSymbols(input: String): List<IndexIgnoringPatternSymbol> =
        getPatternSymbols(input)
            .map { it.unindexed }
            // .map { ups -> ups.move(positionedBrackets.filter { pb -> pb.position <= ups.index }.size) }
            .toList()

    fun transferToNewRegex(newRegex: CombinedRegex): CapturingGroupModel {
        if (capturingGroups.isEmpty()) {
            return CapturingGroupModel(regex = newRegex, emptyList())
        }

        val oldPatternParts = getMovedPatternSymbols(this.regex.pattern)
        val newPatternParts = getMovedPatternSymbols(newRegex.pattern)

        val differences = findDifferences(input1 = oldPatternParts, input2 = newPatternParts)
            .map { it.move(positionedBrackets.filter { pb -> pb.position <= it.range.first }.size) }
            .sortedBy { it.range.first }
        console.log(differences.toIndexedString())
        var newCapturingGroups = capturingGroups.toMutableList()

        console.log(newCapturingGroups.toIndexedString("Start: "))
        differences.forEach { difference ->
            when (difference.type) {
                Difference.Type.Add -> {
                    newCapturingGroups
                        .map { cg ->
                            when {
                                difference.range.first <= cg.openingPosition -> cg.move(difference.range.length)
                                difference.range.first > cg.openingPosition
                                        && difference.range.first < cg.closingPosition -> cg.move(
                                    0,
                                    difference.range.length
                                )

                                else -> cg
                            }
                        }
                        .forEachIndexed { index, capturingGroup ->
                            newCapturingGroups[index] = capturingGroup
                        }
                }

                Difference.Type.Remove -> {
                    newCapturingGroups
                        .map { cg ->
                            if (difference.range.first < cg.openingPosition) {
                                console.log("remove", 1)
                                cg.move(-difference.range.length)
                            } else if (difference.range.first >= cg.openingPosition
                                && difference.range.first < cg.closingPosition
                            ) {
                                console.log("remove", 2)
                                cg.move(0, -difference.range.length)
                            } else {
                                cg
                            }
                        }
                        .mapIndexed { index, capturingGroup ->
                            newCapturingGroups[index] = capturingGroup
                            capturingGroup
                        }
                        .filter { it.openingPosition >= it.closingPosition - 1 }
                        .forEach {
                            newCapturingGroups = removeCapturingGroupInternal(newCapturingGroups, it).toMutableList()
                        }
                    console.log(newCapturingGroups.toIndexedString("ende: "))
                }
            }
        }

        return copy(regex = newRegex, capturingGroups = newCapturingGroups)
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

        internal val unindexed = IndexIgnoringPatternSymbol(this)
    }

    internal class IndexIgnoringPatternSymbol(
        private val symbol: PatternSymbol,
        private val move: Int = 0
    ) {
        val index = symbol.index + move
        val text = symbol.text
        val type = symbol.type

        fun move(amount: Int): IndexIgnoringPatternSymbol = IndexIgnoringPatternSymbol(symbol, move + amount)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.js != other::class.js) return false

            other as IndexIgnoringPatternSymbol

            if (text != other.text) return false
            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            var result = text.hashCode()
            result = 31 * result + type.hashCode()
            return result
        }
    }

    internal enum class PatternSymbolType {
        GROUP_START, GROUP_END, COMPLETE, ALTERNATIVE, CHARACTER
    }

    private fun getPatternPartType(groups: MatchGroupCollection): PatternSymbolType {
        return when {
            groups["part"]?.value != null -> PatternSymbolType.CHARACTER
            groups["open"]?.value != null -> PatternSymbolType.GROUP_START
            groups["close"]?.value != null -> PatternSymbolType.GROUP_END
            groups["alt"]?.value != null -> PatternSymbolType.ALTERNATIVE
            groups["complete"]?.value != null -> PatternSymbolType.COMPLETE
            else -> throw RegexGeneratorException("Unable to recognize pattern part type from: $groups")
        }
    }

    @ConsistentCopyVisibility
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

        private val openingString: String = "(${name?.let { "?<$it>" } ?: ""}"
        private val closingString: String = ")${quantifier ?: ""}"

        val positionedBrackets: List<PositionedBracket> = listOf(
            PositionedBracket(position = openingPosition, content = openingString),
            PositionedBracket(position = closingPosition, content = closingString)
        )

        val range: IntRange = IntRange(openingPosition, closingPosition)

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

    data class PositionedBracket(
        val position: Int,
        val content: String,
    )

    data class Flags(
        val caseSensitive: Boolean? = null,
        val unixLines: Boolean? = null,
        val multiline: Boolean? = null,
        val dotAll: Boolean? = null,
        val unicodeCase: Boolean? = null,
        val comments: Boolean? = null,
    )
}
