package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.em
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.injector.CustomCapture
import kotlinx.html.injector.inject
import kotlinx.html.input
import kotlinx.html.js.form
import kotlinx.html.js.li
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.span
import kotlinx.html.span
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel.CapturingGroup
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel.PatternPart
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel.PatternPartGroup
import org.olafneumann.regex.generator.capgroup.CapturingGroupModel.PatternSymbol
import org.olafneumann.regex.generator.js.Popover
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.MouseCapture
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

@Suppress("TooManyFunctions")
internal class P4CapturingGroups(
    private val controller: MVCContract.Controller
) : NumberedExpandablePart(
    elementId = "rg_capgroup_selection_container",
    caption = "Add Capturing Groups (optional)",
    initialStateOpen = true,
) {
    companion object {
        private const val CLASS_SELECTION = "bg-warning"
        private const val CLASS_HIGHLIGHT = "bg-info"
    }

    private val textDisplay = HtmlHelper.getElementById<HTMLDivElement>("rg_cap_group_display")
    private val capGroupList = HtmlHelper.getElementById<HTMLUListElement>("rg_cap_group_list")

    private var capturingGroupModel: CapturingGroupModel by Delegates.notNull()

    private var popover: Popover? = null
    private var clearMarks: () -> Unit = {}

    init {
        document.onmousedown = {
            // dispose any popover if the user click somewhere else
            disposePopover()
        }
    }

    private fun disposePopover() {
        popover?.dispose()
        popover = null
        clearMarks()
    }

    fun applyModel(model: DisplayModel) {
        setCapturingGroupModel(model.patternRecognizerModel.capturingGroupModel)
    }

    private fun setCapturingGroupModel(capturingGroupModelX: CapturingGroupModel) {
        this.capturingGroupModel = capturingGroupModelX

        textDisplay.clear()
        capGroupList.clear()

        // show text to select regular expressions
        val root = capturingGroupModel.rootPatternPartGroup
        val spans = createSpans(group = root)
            .entries
            .map { MarkerItems(it.key.index, it.key, it.value) }
        spans.forEach { textDisplay.appendChild(it.span) }
        addMouseListenerToSpans(spans)
        clearMarks = {
            spans.forEach { it.span.classList.toggle(CLASS_SELECTION, false) }
            markedRange = null
        }

        // display existing regular expressions
        createCapturingGroupList(spans)
    }

    private fun createCapturingGroup(name: String?, elementRange: IntRange) {
        disposePopover()
        controller.onNewCapturingGroupModel(
            capturingGroupModel.addCapturingGroup(
                start = elementRange.first,
                endInclusive = elementRange.last,
                name = name
            )
        )
    }

    private fun removeCapturingGroup(capturingGroup: CapturingGroup) {
        controller.onNewCapturingGroupModel(capturingGroupModel.removeCapturingGroup(capturingGroup.id))
    }

    private fun createCapturingGroupList(items: List<MarkerItems>) {
        if (capturingGroupModel.capturingGroups.isEmpty()) {
            capGroupList.appendChild(document.create.li("list-group-item rg-cap-group-list-item rg-faded") {
                em { +"No capturing groups defined yet." }
            })
        }

        capturingGroupModel.capturingGroups
            .map { capturingGroup ->
                document.create.li(
                    classes = "list-group-item rg-cap-group-list-item d-flex justify-content-between"
                ) {
                    span {
                        if (capturingGroup.name != null) {
                            span(classes = "rg_cap_group_named") { +capturingGroup.name }
                        } else {
                            span(classes = "rg_cap_group_unnamed") { +"unnamed group" }
                        }

                        span {
                            +"(${capturingGroup.openingPosition} to ${capturingGroup.closingPosition})"
                        }
                    }

                    div {
                        a(classes = "btn") {
                            i(classes = "bi bi-trash") {}
                            onClickFunction = { _ ->
                                removeCapturingGroup(capturingGroup)
                                setCapturingGroupModel(capturingGroupModel)
                            }
                        }
                    }

                    // highlight capturing group range in text
                    var isMarking = false
                    val markIt: (Boolean, IntRange?) -> Unit = { selected, range ->
                        items.forEach { item ->
                            item.span.classList.toggle(
                                token = CLASS_HIGHLIGHT,
                                force = selected && (range == null || item.index in range)
                            )
                        }
                    }
                    onMouseMoveFunction = { _ ->
                        if (!isMarking) {
                            markIt(true, capturingGroup.range)
                            isMarking = true
                        }
                    }
                    onMouseOutFunction = {
                        markIt(false, null)
                        isMarking = false
                    }
                }
            }
            .forEach { capGroupList.appendChild(it) }
    }

    private class InjectById(private val id: String) : CustomCapture {
        override fun apply(element: HTMLElement): Boolean = element.id == id
    }

    private class Elements {
        var nameText: HTMLInputElement by Delegates.notNull()
    }

    private fun createSpans(group: PatternPartGroup): MutableMap<PatternSymbol, HTMLSpanElement> {
        return group.parts
            .map { createSpans(it) }
            .fold<Map<PatternSymbol, HTMLSpanElement>, MutableMap<PatternSymbol, HTMLSpanElement>>(
                initial = mutableMapOf(),
                operation = { acc, map ->
                    acc.putAll(map)
                    acc
                })
    }

    private fun createSpans(patternPart: PatternPart): Map<PatternSymbol, HTMLSpanElement> =
        when (patternPart) {
            is PatternPartGroup -> {
                createSpans(group = patternPart)
            }

            is PatternSymbol -> {
                mapOf(patternPart to createSpan(patternPart))
            }

            else -> {
                error("Unknown part type $patternPart")
            }
        }

    private fun createSpan(patternSymbol: PatternSymbol): HTMLSpanElement =
        document.create.span(classes = "rg-result-part") {
            +patternSymbol.text
            attributes["data-index"] = patternSymbol.index.toString()
        }

    private fun addMouseListenerToSpans(items: List<MarkerItems>) {
        items.forEach { item ->
            var range: IntRange? = null

            item.span.onmousedown = { mouseDownEvent ->
                disposePopover()

                val startIndex = item.patternSymbol.index
                val mouseMoveListener = { mouseMoveEvent: MouseEvent ->
                    MouseCapture.restoreGlobalMouseEvents()
                    val element = document.elementFromPoint(
                        x = mouseMoveEvent.x,
                        y = mouseMoveEvent.y
                    )
                    MouseCapture.preventGlobalMouseEvents()
                    if (element != null && element is HTMLSpanElement) {
                        val currentIndex = element.attributes["data-index"]?.value?.toInt()
                        currentIndex?.let { ci -> range = mark(items, startIndex, ci) }
                    }
                }
                MouseCapture.capture(
                    event = mouseDownEvent, // as MouseEvent,
                    mouseMoveListener = mouseMoveListener,
                    mouseUpListener = {
                        range?.let { range ->
                            onMarkedRegion(
                                range = range,
                                element = items[range.first].span
                            )
                        }
                    }
                )
                // run the first event now
                mouseMoveListener(mouseDownEvent)
            }
        }
    }

    private var markedRange: IntRange? = null

    private var lastRange: IntRange? = null
    private fun mark(items: List<MarkerItems>, from: Int, to: Int): IntRange? {
        console.log("mark", from, to)
        val range = IntRange(min(from, to), max(from, to))
        if (lastRange != null && lastRange == range) {
            return markedRange
        }
        lastRange = range
        return mark__(items, range)
    }

    private fun mark__(items: List<MarkerItems>, range: IntRange): IntRange? {
        console.log("mark__", range)
        val userStart = items[range.first].patternSymbol
        val userEnd = items[range.last].patternSymbol
        val realFrom = if (userStart.selectable) range.first else range.firstOrNull { items[it].patternSymbol.selectable }
        val realTo = if (userEnd.selectable) range.last else (range.last downTo range.first).firstOrNull { items[it].patternSymbol.selectable }

        if (realFrom == null || realTo == null) {
            return null
        }

        val compStart = items[realFrom].patternSymbol
        val compEnd = items[realTo].patternSymbol
        val range = findIndicesInCommentParent(compStart, compEnd)

        if (markedRange == null || markedRange != range) {
            if (range != null) {
                items.forEach {
                    it.span.classList.toggle(CLASS_SELECTION, it.index in range)
                }
                markedRange = range
            } else {
                clearMarks()
            }

        }
        return range
    }

    private fun findIndicesInCommentParent(one: PatternPart, other: PatternPart): IntRange? {
        return if (one.isRoot || other.isRoot) {
            console.warn("One of the symbols is the root node. This should not happen.", one, other)
            //val root = if (one.isRoot) one else other
            //IntRange(root.firstIndex, root.lastIndex)
            null
        } else if (one.parent == other.parent && !one.parent!!.isAlternative && one.selectable && other.selectable) {
            IntRange(one.firstIndex, other.lastIndex)
        } else if (one.depth > other.depth) {
            findIndicesInCommentParent(one = one.parent!!, other = other)
        } else {
            findIndicesInCommentParent(one = one, other = other.parent!!)
        }
    }

    private fun onMarkedRegion(range: IntRange, element: HTMLElement) {
        val elements = Elements()
        val idCapGroupName = "rg_name_of_capturing_group"

        val createCapturingGroup: () -> Unit = {
            createCapturingGroup(elements.nameText.value.ifBlank { null }, range)
        }

        popover = Popover(
            element = element,
            html = true,
            contentElement = document.create.inject(
                elements, listOf(
                    InjectById(idCapGroupName) to Elements::nameText
                )
            ).form {
                div(classes = "form-group") {
                    input(type = InputType.text, classes = "form-control-sm") {
                        this.id = idCapGroupName
                        placeholder = "Name (optional)"
                        onKeyDownFunction = { event ->
                            if (event is KeyboardEvent) {
                                if (event.key == "Enter") {
                                    createCapturingGroup()
                                } else if (event.key == "Escape") {
                                    disposePopover()
                                }
                            }
                        }
                    }
                }
                div(classes = "form-group") {
                    button(classes = "btn btn-primary") {
                        +"Create capturing group"
                        type = ButtonType.button
                        onClickFunction = { _ ->
                            createCapturingGroup()
                        }
                    }
                }
            },
            placement = "left",
            title = "Create capturing group",
            trigger = "manual",
            onShown = { elements.nameText.focus() }
        )
        popover!!.show()
        jQuery(".popover").mousedown {
            // prevent popover from being disposed when clicking inside
            it.stopPropagation()
        }
    }

    private data class MarkerItems(
        val index: Int,
        val patternSymbol: PatternSymbol,
        val span: HTMLSpanElement,
    )
}
