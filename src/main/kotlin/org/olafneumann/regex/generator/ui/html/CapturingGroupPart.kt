package org.olafneumann.regex.generator.ui.html

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.dom.create
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseUpFunction
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
import kotlin.js.RegExp
import kotlin.js.json
import kotlin.math.max
import kotlin.math.min

internal class CapturingGroupPart(
    private val presenter: DisplayContract.Controller
) {
    private val container = HtmlHelper.getElementById<HTMLDivElement>("rg_capgroup_selection_container")
    private val number = container.getElementsByTagName("h3")[0] as HTMLHeadingElement
    private val expandButton = HtmlHelper.getElementById<HTMLElement>("rg_cap_group_expander")
    private val expandImageOpen = expandButton.getElementsByTagName("img")[0] as HTMLImageElement
    private val expandImageClose = expandButton.getElementsByTagName("img")[1] as HTMLImageElement
    private val content = HtmlHelper.getElementById<HTMLDivElement>("rg_cap_group_content")
    private val textDisplay = HtmlHelper.getElementById<HTMLDivElement>("rg_text_display_B")

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


    //    (?:
//    (?<br>\((?:\?(?::|<[a-z][a-z0-9]*>|[-a-z]+\)))?)
//    | (?<re>
//    (?: \\.
//    | \[(?:[^\]\\]|\\.)+\]
//    | \)
//    | .
//    )
//    # quantifiers
//    (?: [+*?]+
//    | \{\d+(?:,\d*)?}
//    | \{(?:\d*,)?\d+}
//    )?
//    )
//    )
    private val regex = Regex(
        """(?<br>\((?:\?(?::|=|<[a-z][a-z0-9]*>|[-a-z]+\)))?)|(?<re>(?:\\.|\[(?:[^\]\\]|\\.)+\]|\)|.)(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)""",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    private val regexp = Regex(
        """(?<open>\((?:\?(?::|=|<[a-z][a-z0-9]*>|[-a-z]+\)))?)|(?<part>(?:\\.|\[(?:[^\]\\]|\\.)+\]|[^|)])(?:[+*?]+|\{\d+(?:,\d*)?\}|\{(?:\d*,)?\d+\})?)|(?<close>\))|(?<alt>\|)""",
        options = setOf(RegexOption.IGNORE_CASE)
    )

    private fun findClosingPart(parts: List<RegexPart>, startIndex: Int = 0): Map<Int, Group> {
        val out = mutableMapOf<Int, Group>()
        var index = startIndex
        val group = Group(index)
        while (index < parts.size) {
            val part = parts[index]
            if (part.isGroupStart) {
                out[index] = group
                out.putAll(findClosingPart(parts, index + 1))
                index = out.size + 1
            } else if (part.isGroupEnd && part.associated == null) {
                val groupStart = parts[startIndex - 1]
                for (subIndex in startIndex until index) {
                    if (parts[subIndex].containingGroup == -1) {
                        parts[subIndex].containingGroup = groupStart.index
                    }
                }
                groupStart.associated = index
                part.associated = groupStart.index
                groupStart.groupContainsAlternatives = parts.subList(startIndex, index - 1)
                    .firstOrNull { it.text == "|" } != null
                return index
            } else {
                out[index] = group
                ++index
            }
        }
        return parts.size
    }

    fun setRegularExpression(regularExpression: RecognizerCombiner.RegularExpression) {
        this.regularExpression = regularExpression
        val text = regularExpression.pattern

        textDisplay.clear()
        var moving = false
        var startIndex: Int? = null
        var endIndex: Int? = null
        val selectableParts = regex.findAll(text)
            .mapIndexed { index, result -> RegexPart(index = index, range = result.range, text = result.value) }
            .toList()
        findClosingPart(selectableParts)
        selectableParts
            .map { part ->
                part to
                        document.create.span(classes = "rg-result-part") {
                            attributes["data-index"] = part.index.toString()
                            part.associated?.let { attributes["data-associated"] = it.toString() }
                            attributes["data-group"] = part.containingGroup.toString()

                            +part.text
                            onMouseDownFunction = { mouseDownEvent ->
                                val startIndex = part.index
                                val mouseMoveListener = { mouseMoveEvent: MouseEvent ->
                                    MouseCapture.restoreGlobalMouseEvents()
                                    val element = document.elementFromPoint(
                                        x = mouseMoveEvent.x,
                                        y = mouseMoveEvent.y //(mouseDownEvent as MouseEvent).y
                                    )
                                    MouseCapture.preventGlobalMouseEvents()
                                    if (element != null && element is HTMLSpanElement) {
                                        val currentIndex = element.attributes["data-index"]?.value?.toInt()
                                        currentIndex?.let { ci -> mark(selectableParts, startIndex, ci) }
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
                            onMouseUpFunction = { _ ->
                                selectableParts.mapNotNull { it.element }
                                    .forEach { it.classList.toggle("bg-warning", false) }
                            }
                        }
            }
            .map {
                it.first.element = it.second
                it.second
            }
            .forEach { textDisplay.appendChild(it) }
    }

    private fun mark(parts: List<RegexPart>, startIndex: Int, endIndex: Int) {
        val startPart = parts[startIndex]
        val endPart = parts[endIndex]



        var computedStartIndex: Int? = null
        var computedEndIndex: Int? = null
        if (startPart == endPart && !startPart.isGroup) {
            // only one part selected
            computedStartIndex = startPart.index
            computedEndIndex = endPart.index
        } else if (startPart == endPart && startPart.isGroup) {
            // a group to be selected
            computedStartIndex = min(startPart.index, startPart.associated!!)
            computedEndIndex = max(startPart.index, startPart.associated!!)
        } else if (startPart.containingGroup == endPart.containingGroup) {
            // easy! both parts on same level. mark everything between
            computedStartIndex = min(startIndex, endIndex)
            computedEndIndex = max(startIndex, endIndex)
            if (parts[computedStartIndex].isGroupEnd) {
                computedStartIndex = parts[computedStartIndex].associated!!
            }
            if (parts[computedEndIndex].isGroupStart) {
                computedEndIndex = parts[computedEndIndex].associated!!
            }
        } else {
            // endIndex: find a parent of the same level as the startIndex
            var parentIndex = endIndex
            while (parentIndex != -1 && parts[parentIndex].containingGroup != parts[startIndex].containingGroup) {
                parentIndex = parts[parentIndex].containingGroup
            }
            if (parentIndex != -1 && startPart.containingGroup == parts[parentIndex].containingGroup) {
                if (endIndex > startIndex) {
                    parentIndex = parts[parentIndex].associated!!
                }
                computedStartIndex = startIndex
                computedEndIndex = parentIndex
            }
//        } else if (startPart.containingGroup < endPart.containingGroup) {
//            // mouse is over a group that needs to be included
//            var end = endPart
//            while (end.containingGroup != -1 && end.containingGroup != startPart.containingGroup) {
//                end = parts[end.containingGroup]
//            }
//            if (end.containingGroup == -1 && startPart.containingGroup != -1) {
//                throw RuntimeException("Group-Stuff wrong 1")
//            }
//            if (startIndex > endIndex) {
//                computedStartIndex = end.index
//                computedEndIndex = startIndex
//            } else if (startIndex < endIndex) {
//                computedStartIndex = startIndex
//                computedEndIndex = end.associated!!
//            }
//        } else if (startPart.containingGroup > endPart.containingGroup) {

        }

        if (computedStartIndex != null && computedEndIndex != null) {
            val range = IntRange(computedStartIndex, computedEndIndex)
            parts.forEach { part ->
                part.element?.classList?.toggle("bg-warning", range.contains(part.index))
            }
            // val list = parts.subList(computedStartIndex, computedEndIndex + 1)
            // val out = list.joinToString(separator = "") { it.text }
            // console.log(computedStartIndex, computedEndIndex, out)
        }
    }

    private fun groupSelectableParts(regexParts: List<RegexPart>, parentSelectablePart: SelectablePart? = null): SelectablePart {
        var index = 0
        while (index < regexParts.size) {
            val regexPart = regexParts[index]
            val selectablePart = SelectablePart(parent = parentSelectablePart)
            
            if (regexPart.isGroupStart) {
                groupSelectableParts(regexParts.subList(index, regexParts.indices.last), selectablePart)
            } else if (regexPart.isGroupEnd) {

            } else {

            }
        }
    }

    private class SelectablePart(
        val parent: SelectablePart? = null
    ) {
        var prev: SelectablePart? = null
        var next: SelectablePart? = null
        var regexParts = mutableListOf<RegexPart>()
    }

    private data class Group(val id: Int) {
        companion object {
            val EMPTY = Group(-1)
        }


        fun add(part: RegexPart) {
            _parts.add(part)
            part.group = this
        }
    }

    private class Item(vararg parts: RegexPart) {
        private val _parts = mutableListOf<RegexPart>()
        val parts: Collection<RegexPart> get() = _parts

        init {
            _parts.addAll(parts)
        }
    }

    private data class RegexPart(
        val index: Int,
        val range: IntRange,
        val text: String
    ) {
        val isGroupStart = text.startsWith("(") && !text.endsWith(")")
        val isGroupEnd = text.startsWith(")")
        val isGroup = isGroupStart || isGroupEnd
        var associated: Int? = null
        var containingGroup: Int = -1
        var group = Group.EMPTY
        var groupContainsAlternatives: Boolean = false
        var element: HTMLSpanElement? = null
    }
}