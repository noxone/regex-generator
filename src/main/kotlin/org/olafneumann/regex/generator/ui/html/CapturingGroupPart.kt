package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.dom.create
import kotlinx.html.js.span
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.ui.DisplayContract
import org.olafneumann.regex.generator.ui.HtmlHelper
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLHeadingElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import kotlin.js.json

internal class CapturingGroupPart(
    private val presenter: DisplayContract.Controller
) {
    private val container = HtmlHelper.getElementById<HTMLDivElement>("rg_capgroup_selection_container")
    private val number = container.getElementsByTagName("h3")[0] as HTMLHeadingElement
    private val expandButton = HtmlHelper.getElementById<HTMLElement>("rg_cap_group_expander")
    private val expandImageOpen = expandButton.getElementsByTagName("img")[0] as HTMLImageElement
    private val expandImageClose = expandButton.getElementsByTagName("img")[1] as HTMLImageElement
    private val content = HtmlHelper.getElementById<HTMLDivElement>("rg_cap_group_content")
    private val textDisplay = HtmlHelper.getElementById<HTMLDivElement>("rg_cap_group_display")

    private var regularExpression: RecognizerCombiner.RegularExpression? = null

    init {
        toggleVisibility(open = false)
        expandButton.onclick = {
            it.preventDefault()
            toggleVisibility(open = !isOpen)
        }
    }

    private val isOpen: Boolean
        get() = jQuery(content).`is`(":visible")

    private fun toggleVisibility(open: Boolean = false) {
        val jContent = jQuery(content)
        if (open) {
            jContent.slideDown()
            jQuery(expandImageOpen).fadeOut()
            jQuery(expandImageClose).fadeIn()
            //jQuery(number).animate(json("font-size" to "4.5rem"))
            jQuery(number).animate(json("margin-top" to 0, "margin-bottom" to 0))
        } else {
            jContent.slideUp()
            jQuery(expandImageOpen).fadeIn()
            jQuery(expandImageClose).fadeOut()
            //jQuery(number).animate(json("font-size" to "1.75rem"))
            jQuery(number).animate(json("margin-top" to "-1.7rem", "margin-bottom" to "-2rem"))
        }
    }

    private val patternPartitionerRegex = Regex(
        """(?<complete>\(\?(?:[a-zA-Z]+|[+-]?[0-9]+|&\w+|P=\w+)\))|(?<open>\((?:\?(?::|!|>|=|\||<=|P?<[a-z][a-z0-9]*>|'[a-z][a-z0-9]*'|[-a-z]+:))?)|(?<part>(?:\\.|\[(?:[^\]\\]|\\.)+\]|[^|)])(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<close>\)(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<alt>\|)""",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    fun setRegularExpression(regularExpression: RecognizerCombiner.RegularExpression) {
        textDisplay.clear()

        val root = analyzeRegexGroups(regularExpression.pattern)
        console.log("ROOT", root)
        val spans = makeSpans(group = root)
        spans.forEach { pair ->
            val symbol = pair.key
            val span = pair.value

            span.onmousedown = { mouseDownEvent ->
                val startIndex = symbol.index
                console.log("DOWN", startIndex, mouseDownEvent)
                val mouseMoveListener = { mouseMoveEvent: MouseEvent ->
                    MouseCapture.restoreGlobalMouseEvents()
                    val element = document.elementFromPoint(
                        x = mouseMoveEvent.x,
                        y = mouseMoveEvent.y
                    )
                    MouseCapture.preventGlobalMouseEvents()
                    if (element != null && element is HTMLSpanElement) {
                        val currentIndex = element.attributes["data-index"]?.value?.toInt()
                        currentIndex?.let { ci -> mark(spans, startIndex, ci) }
                    }
                }
                MouseCapture.capture(
                    event = mouseDownEvent as MouseEvent,
                    mouseMoveListener = mouseMoveListener,
                    mouseUpListener = {
                        console.log("up", it)
                    }
                )
                // run the first event now
                mouseMoveListener(mouseDownEvent)
            }
            span.onmouseup = { mouseUpEvent ->
                console.log("UP", mouseUpEvent)
            }
            textDisplay.appendChild(span)
        }
    }

    private fun mark(items: Map<PatternSymbol, HTMLSpanElement>, from: Int, to: Int) {
        if (from > to) {
            mark(items = items, from = to, to = from)
            return
        }

        var realFrom = from
        var realTo = to

        val userStart = items.entries.first { it.key.index == from }.key
        val userEnd = items.entries.first { it.key.index == to }.key
        realFrom = if (userStart.selectable) realFrom else userStart.parent!!.firstIndex
        realTo = if (userEnd.selectable) realTo else userEnd.parent!!.lastIndex

        val compStart = items.entries.first { it.key.index == realFrom }.key
        val compEnd = items.entries.first { it.key.index == realTo }.key
        val range = findIndicesInCommentParent(compStart, compEnd)

        items.forEach {
            it.value.classList.toggle("bg-warning", it.key.index in range)
        }
    }

    private fun findIndicesInCommentParent(one: PatternPart, other: PatternPart): IntRange {
        return if (one.isRoot || other.isRoot) {
            console.log("One of the symbols is the root node. This should not happen.", one, other)
            val root = if (one.isRoot) one else other
            //root.firstIndex to root.lastIndex
            IntRange(root.firstIndex, root.lastIndex)
        } else if (one.parent == other.parent) {
            //one.firstIndex to other.lastIndex
            IntRange(one.firstIndex, other.lastIndex)
        } else if (one.depth > other.depth) {
            findIndicesInCommentParent(one = one.parent!!, other = other)
        } else {
            findIndicesInCommentParent(one = one, other = other.parent!!)
        }
    }

    private fun makeSpans(group: PatternPartGroup): Map<PatternSymbol, HTMLSpanElement> {
        //val out: List<Pair<PatternPart, HTMLSpanElement>> =
        return group.parts
            .map { part ->
                when (part) {
                    is PatternPartGroup -> {
                        makeSpans(group = part)
                    }

                    is PatternSymbol -> {
                        mapOf(part to document.create.span(classes = "rg-result-part") {
                            +part.text
                            attributes["data-index"] = part.index.toString()
                        })
                    }

                    else -> {
                        //throw Exception("Unknown part type $part")
                        //emptyList()
                        emptyMap()
                    }
                }
            }
            .flatMap { it.entries }
            .associate { it.key to it.value }
    }

    private fun analyzeRegexGroups(pattern: String): PatternPartGroup {
        val rawParts = patternPartitionerRegex.findAll(pattern)
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
                // TODO is group with negative lookahead (?!...), do not allow selection of children
            } else if (part.type == PatternSymbolType.GROUP_END) {
                currentLevel.add(part)
                if (currentLevel.parent == null) {
                    throw Exception("Unbalanced number of opening and closing brackets.")
                }
                currentLevel = currentLevel.parent!!
            } else if (part.type == PatternSymbolType.CHARACTER) {
                currentLevel.add(part)
                part.selectable = true
            } else if (part.type == PatternSymbolType.ALTERNATIVE || part.type == PatternSymbolType.COMPLETE) {
                currentLevel.add(part)
            } else {
                throw Exception("Unknown pattern symbol: ${part.type}")
            }
        }
        return root
    }

    private abstract class PatternPart {
        abstract var parent: PatternPartGroup? // TODO make 'val' again
        abstract val isRoot: Boolean
        abstract val depth: Int

        abstract val firstIndex: Int
        abstract val lastIndex: Int

        abstract val selectable: Boolean

        open fun add(part: PatternPart) {
            throw Exception("'add' not implemented")
        }
    }

    private class PatternPartGroup(
        override var parent: PatternPartGroup? = null
    ) : PatternPart() {
        override val isRoot: Boolean get() = parent == null
        override val depth: Int get() = if (isRoot) 0 else 1 + parent!!.depth

        override val firstIndex: Int
            get() = if (parts.isEmpty()) parent!!.firstIndex else parts.first().firstIndex
        override val lastIndex: Int
            get() = if (parts.isEmpty()) parent!!.lastIndex else parts.last().lastIndex

        private var alternative = false
        val isAlternative: Boolean
            get() = alternative

        var forcedNotSelectable: Boolean = false
        override val selectable: Boolean
            get() = !forcedNotSelectable && (parent?.forcedNotSelectable ?: true)

        val parts: List<PatternPart> get() = mutableParts
        private val mutableParts = mutableListOf<PatternPart>()

        override fun add(part: PatternPart) {
            if (part is PatternSymbol && part.type == PatternSymbolType.ALTERNATIVE) {
                if (!alternative) {
                    // needs to turned into an alternative
                    val firstSubGroup = PatternPartGroup(parent = this)
                    firstSubGroup.mutableParts.addAll(this.mutableParts.onEach { it.parent = firstSubGroup })
                    mutableParts.clear()
                    mutableParts.add(firstSubGroup)
                    alternative = true
                }

                mutableParts.add(part)
                part.parent = this
                mutableParts.add(PatternPartGroup(parent = this))
            } else {
                if (alternative) {
                    val partParent = mutableParts.last() as PatternPartGroup
                    partParent.add(part)
                    part.parent = partParent
                } else {
                    mutableParts.add(part)
                    part.parent = this
                }
            }
        }
    }

    private data class PatternSymbol(
        val index: Int,
        val range: IntRange,
        val text: String,
        val type: PatternSymbolType
    ) : PatternPart() {
        override var parent: PatternPartGroup? = null
        private var _selectable = false
        override var selectable: Boolean
            set(value) { _selectable = value }
            get() = parent!!.selectable && _selectable

        //override var selectable: Boolean = false
        override val isRoot: Boolean = false
        override val depth: Int get() = 1 + parent!!.depth

        override val firstIndex: Int = index
        override val lastIndex: Int = index
    }

    private enum class PatternSymbolType {
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
            throw IllegalArgumentException("Unable to recognize pattern part type from: $groups")
        }
    }
}