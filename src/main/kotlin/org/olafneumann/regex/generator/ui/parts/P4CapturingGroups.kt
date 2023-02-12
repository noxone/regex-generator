package org.olafneumann.regex.generator.ui.parts

import kotlinx.browser.document
import kotlinx.dom.clear
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.create
import kotlinx.html.em
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.injector.CustomCapture
import kotlinx.html.injector.InjectByClassName
import kotlinx.html.injector.inject
import kotlinx.html.input
import kotlinx.html.js.form
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onInputFunction
import kotlinx.html.js.onKeyDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.span
import kotlinx.html.label
import kotlinx.html.org.w3c.dom.events.Event
import kotlinx.html.span
import kotlinx.html.title
import org.olafneumann.regex.generator.js.Popover
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.model.CapturingGroupModel
import org.olafneumann.regex.generator.model.CapturingGroupModel.CapturingGroup
import org.olafneumann.regex.generator.model.CapturingGroupModel.PatternPart
import org.olafneumann.regex.generator.model.CapturingGroupModel.PatternPartGroup
import org.olafneumann.regex.generator.model.CapturingGroupModel.PatternSymbol
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.DoubleWorkPrevention
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.MouseCapture
import org.olafneumann.regex.generator.ui.utils.gButton
import org.olafneumann.regex.generator.utils.center
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
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

    private val textDisplay = HtmlHelper.getElementById<HTMLElement>("rg_cap_group_display")
    private val capGroupList = HtmlHelper.getElementById<HTMLElement>("rg_cap_group_list")

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
            .map { MarkerItem(it.key.index, it.key, it.value) }
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
        controller.onNewCapturingGroupModel(capturingGroupModel.removeCapturingGroup(capturingGroup))
    }

    private fun renameCapturingGroup(capturingGroup: CapturingGroup, newName: String?) {
        controller.onNewCapturingGroupModel(capturingGroupModel.renameCapturingGroup(capturingGroup, newName))
    }

    private fun setCapturingGroupQuantifier(capturingGroup: CapturingGroup, quantifier: String?) {
        controller.onNewCapturingGroupModel(
            capturingGroupModel.setCapturingGroupQuantifiers(
                capturingGroup,
                quantifier
            )
        )
    }

    private fun createCapturingGroupList(items: List<MarkerItem>) {
        if (capturingGroupModel.capturingGroups.isEmpty()) {
            capGroupList.appendChild(document.create.div("col rg-cap-group-list-item rg-faded") {
                em { +"No capturing groups defined yet." }
            })
        }

        capturingGroupModel.capturingGroups
            .map { capturingGroup ->
                val elements = InlineElements()

                document.create.inject(
                    elements, listOf(
                        InjectByClassName("rg-btn-rename") to InlineElements::renameButton,
                        InjectByClassName("rg-btn-quantifiers") to InlineElements::quantifiersButton,
                        InjectByClassName("rg-btn-flags") to InlineElements::flagsButton,
                    )
                ).div(classes = "col-12 col-md-6 col-xl-4 col-xxl-3") {
                    val highlighter = Highlighter(items, capturingGroup)
                    div(
                        classes = "rg-capturing-group border rounded p-1 d-flex " +
                                "justify-content-between align-items-center"
                    ) {
                        div(classes = "ms-2 text-truncate") {
                            if (capturingGroup.name != null) {
                                span {
                                    +capturingGroup.name
                                    title = capturingGroup.name
                                }
                            } else {
                                span(classes = "rg-cap-group-unnamed") { +"Unnamed group" }
                            }
                        }

                        div(classes = "btn-group") {
                            gButton(
                                "text-secondary rg-btn-rename",
                                "Rename Capturing Group${capturingGroup.name?.let { " '$it'" } ?: ""}",
                                { onRenameCapturingGroup(capturingGroup, elements.renameButton) }
                            ) { i(classes = "bi bi-input-cursor-text") }
                            gButton(
                                "text-secondary text-nowrap rg-btn-quantifiers",
                                "Adjust quantifier",
                                { showQuantifierPopover(capturingGroup, elements.quantifiersButton) }
                            ) { +(capturingGroup.quantifier ?: "{1}") }
                            gButton(
                                "text-secondary text-nowrap rg-btn-flags d-none",
                                "Set flags",
                                { showFlagsPopover(element = elements.flagsButton) }
                            ) { i(classes = "bi bi-flag-fill") }
                            gButton(
                                "text-danger",
                                "Delete capturing group${capturingGroup.name?.let { " '$it'" } ?: ""}",
                                { removeCapturingGroup(capturingGroup) }
                            ) { i(classes = "bi bi-trash") }
                        }

                        onMouseMoveFunction = highlighter.onMouseMoveFunction
                        onMouseOutFunction = highlighter.onMouseOutFunction
                    }
                }
            }
            .forEach { capGroupList.appendChild(it) }
    }

    private class InjectById(private val id: String) : CustomCapture {
        override fun apply(element: HTMLElement): Boolean = element.id == id
    }

    private class InlineElements {
        var renameButton: HTMLButtonElement by Delegates.notNull()
        var quantifiersButton: HTMLButtonElement by Delegates.notNull()
        var flagsButton: HTMLButtonElement by Delegates.notNull()
    }

    private class PopoverElements {
        var nameText: HTMLInputElement by Delegates.notNull()
    }

    private fun createSpans(group: PatternPartGroup): MutableMap<PatternSymbol, HTMLSpanElement> {
        return group.parts
            .map { createSpans(it) }
            .fold(
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

    private fun addMouseListenerToSpans(items: List<MarkerItem>) {
        items.forEach { item ->
            item.span.onmousedown = { mouseDownEvent ->
                disposePopover()

                var range: IntRange? = null
                val indexCache = DoubleWorkPrevention<Int> { index ->
                    index?.let { range = mark(items = items, originalFrom = item.patternSymbol.index, originalTo = it) }
                }
                val elementCache = DoubleWorkPrevention<HTMLSpanElement> { element ->
                    element?.attributes
                        ?.get("data-index")
                        ?.value
                        ?.toIntOrNull()
                        ?.let { index -> indexCache.set(index) }
                }

                val mouseMoveListener = { mouseMoveEvent: MouseEvent ->
                    val element = MouseCapture.withGlobalMouseEvents {
                        document.elementFromPoint(x = mouseMoveEvent.x, y = mouseMoveEvent.y)
                    }
                    if (element != null && element is HTMLSpanElement) {
                        elementCache.set(element)
                    }
                }
                val mouseUpListener: (MouseEvent) -> Unit = { _ ->
                    range?.let { range ->
                        onMarkedRegion(range = range, element = items[range.center].span)
                    }
                }
                MouseCapture.capture(
                    event = mouseDownEvent, // as MouseEvent,
                    mouseMoveListener = mouseMoveListener,
                    mouseUpListener = mouseUpListener
                )
                // run the first event now
                mouseMoveListener(mouseDownEvent)
            }
        }
    }

    private var markedRange: IntRange? = null

    private fun mark(items: List<MarkerItem>, originalFrom: Int, originalTo: Int): IntRange? {
        val originalRange = IntRange(min(originalFrom, originalTo), max(originalFrom, originalTo))

        val from = originalRange.firstOrNull { items[it].patternSymbol.selectable }
        val to = originalRange.reversed().firstOrNull { items[it].patternSymbol.selectable }
        if (from == null || to == null) {
            return null
        }

        val range = findIndicesInCommentParent(items[from].patternSymbol, items[to].patternSymbol)
        if (markedRange != range) {
            markRange(items = items, range = range)
        }
        return range
    }

    private fun markRange(items: Collection<MarkerItem>, range: IntRange?) {
        if (range != null) {
            items.forEach {
                it.span.classList.toggle(CLASS_SELECTION, it.index in range)
            }
        } else {
            clearMarks()
        }
        markedRange = range
    }

    private fun findIndicesInCommentParent(one: PatternPart, other: PatternPart): IntRange? {
        return if (one.isRoot || other.isRoot) {
            console.warn("One of the symbols is the root node. This should not happen.", one, other)
            null
        } else if (one.parent == other.parent && !one.parent!!.isAlternative && one.selectable && other.selectable) {
            IntRange(one.firstIndex, other.lastIndex)
        } else if (one.depth > other.depth) {
            findIndicesInCommentParent(one = one.parent!!, other = other)
        } else {
            findIndicesInCommentParent(one = one, other = other.parent!!)
        }
    }

    private fun onMarkedRegion(range: IntRange, element: HTMLElement) =
        showCapturingGroupNamePopover("Create Capturing Group", element) { capturingGroupName ->
            disposePopover()
            createCapturingGroup(capturingGroupName, range)
        }

    private fun onRenameCapturingGroup(capturingGroup: CapturingGroup, element: HTMLElement) {
        showCapturingGroupNamePopover(
            "Rename",
            element,
            prefilledValue = capturingGroup.name ?: "",
            Popover.Placement.Bottom
        ) { capturingGroupName ->
            disposePopover()
            if (capturingGroup.name != capturingGroupName) {
                renameCapturingGroup(capturingGroup, capturingGroupName)
            }
        }
    }

    private fun showCapturingGroupNamePopover(
        caption: String,
        element: HTMLElement,
        prefilledValue: String = "",
        popoverPlacement: Popover.Placement = Popover.Placement.Top,
        action: (String?) -> Unit
    ) {
        val elements = PopoverElements()
        val idCapGroupName = "rg_name_of_capturing_group"

        val getNewCapturingGroupName: () -> String? = { elements.nameText.value.trim().ifBlank { null } }
        val isNewCapturingGroupNameValid: () -> Boolean =
            { CapturingGroupModel.isCapturingGroupNameValid(getNewCapturingGroupName()) }

        popover = Popover(
            element = element,
            placement = popoverPlacement,
            title = caption,
            trigger = Popover.Trigger.Manual,
            onShown = { elements.nameText.select() },
            onCloseButtonClick = { disposePopover() },
        ) {
            inject(
                elements, listOf(
                    InjectById(idCapGroupName) to PopoverElements::nameText
                )
            ).form(classes = "needs-validation") {
                autoComplete = false
                div(classes = "mb-3") {
                    label(classes = "form-label") {
                        htmlFor = idCapGroupName
                        +"Name (optional)"
                    }
                    input(type = InputType.text, classes = "form-control") {
                        this.id = idCapGroupName
                        //placeholder = "Name (optional)"
                        value = prefilledValue
                        autoComplete = false
                        onInputFunction = {
                            elements.nameText.classList.toggle("is-invalid", !isNewCapturingGroupNameValid())
                            elements.nameText.classList.toggle("is-valid", getNewCapturingGroupName() != null)
                        }
                        onKeyDownFunction = { event ->
                            if (event is KeyboardEvent) {
                                if (event.key == "Enter") {
                                    action(getNewCapturingGroupName())
                                } else if (event.key == "Escape") {
                                    disposePopover()
                                }
                            }
                        }
                    }
                    div(classes = "invalid-feedback") {
                        +"The name is invalid. Ensure it is alpha numeric and does not begin with a digit."
                    }
                }

                button(classes = "btn btn-primary", type = ButtonType.button) {
                    +caption
                    onClickFunction = {
                        action(getNewCapturingGroupName())
                    }
                }
            }
        }
        popover!!.show()
        jQuery(".popover").mousedown {
            // prevent popover from being disposed when clicking inside
            it.stopPropagation()
        }
    }

    private fun showQuantifierPopover(
        capturingGroup: CapturingGroup,
        element: HTMLElement,
    ) {
        val activeClass: (String?) -> String = { quantifier ->
            if (capturingGroup.quantifier == quantifier) {
                "active"
            } else {
                ""
            }
        }
        val setQuantifier: (String?) -> Unit = { quantifier ->
            setCapturingGroupQuantifier(capturingGroup, quantifier)
            disposePopover()
        }
        popover = Popover(
            element = element,
            placement = Popover.Placement.Bottom,
            title = "Quantifier",
            trigger = Popover.Trigger.Manual,
            onCloseButtonClick = { disposePopover() },
        ) {
            form {
                autoComplete = false
                div(classes = "mb-3") {
                    fun DIV.qButton(caption: String, description: String, quantifier: String?) {
                        button(
                            classes = "btn btn-light btn-toggle border ${activeClass(quantifier)}",
                            type = ButtonType.button
                        ) {
                            +caption
                            title = description
                            onClickFunction = { setQuantifier(quantifier) }
                        }
                    }
                    div(classes = "mb-3") {
                        label(classes = "form-label") { +"Exact quantifiers" }
                        div(classes = "input-group") {
                            qButton("{1}", "Matches exactly once", null)
                            /*button(classes = "btn btn-light btn-toggle border", type = ButtonType.button) {
                                +"{x,y}"
                                title = "Matches between X and Y times"
                                onClickFunction = {  }
                            }
                            input(type = InputType.number, classes = "form-control") {
                                autoComplete = false
                                min = "0"
                                value = "3"
                                onInputFunction = {}
                                onKeyDownFunction = {}
                            }
                            span(classes = "input-group-text") { +"-" }
                            input(type = InputType.number, classes = "form-control") {
                                autoComplete = false
                                min = "0"
                                value = "5"
                                onInputFunction = {}
                                onKeyDownFunction = {}
                            }*/
                        }
                    }
                    div(classes = "mb-3") {
                        label(classes = "form-label") { +"Greedy quantifiers" }
                        div(classes = "btn-group d-block") {
                            qButton("?", "Matches zero or one time", "?")
                            qButton("*", "Matches zero or more times", "*")
                            qButton("+", "Matches one or more times", "+")
                        }
                    }
                    div(classes = "mb-3") {
                        label(classes = "form-label") { +"Lazy quantifiers" }
                        div(classes = "btn-group d-block") {
                            qButton("??", "Matches zero or one time, but as few times as possible", "??")
                            qButton("*?", "Matches zero or more times, but as few times as possible", "*?")
                            qButton("+?", "Matches one or more times, but as few times as possible", "+?")
                        }
                    }
                }
            }
        }
        popover!!.show()
        jQuery(".popover").mousedown {
            // prevent popover from being disposed when clicking inside
            it.stopPropagation()
        }
    }

    private fun showFlagsPopover(
        element: HTMLElement,
    ) {
        popover = Popover(
            element = element,
            placement = Popover.Placement.Bottom,
            title = "Flags",
            trigger = Popover.Trigger.Manual,
            onCloseButtonClick = { disposePopover() },
        ) {
            form {
                div(classes = "mb-3") {
                    label(classes = "form-label") {
                        +"Flags"
                    }
                    div(classes = "d-block") {
                        div(classes = "btn-group") {
                            button(classes = "btn btn-light") {
                                +"i"
                                title = "Case Sensitive"
                            }
                            button(classes = "btn btn-light") {
                                +"d"
                                title = "Unix Lines"
                            }
                            button(classes = "btn btn-light") {
                                +"m"
                                title = "Multiline"
                            }
                            button(classes = "btn btn-light") {
                                +"s"
                                title = "DotAll"
                            }
                            button(classes = "btn btn-light") {
                                +"u"
                                title = "Unicode Case"
                            }
                            button(classes = "btn btn-light") {
                                +"x"
                                title = "Comments"
                            }
                        }
                    }
                }

                button(classes = "btn btn-primary") {
                    +"Save"
                    type = ButtonType.button
                    onClickFunction = { }
                }
            }
        }
        popover!!.show()
        jQuery(".popover").mousedown {
            // prevent popover from being disposed when clicking inside
            it.stopPropagation()
        }
    }

    private data class MarkerItem(
        val index: Int,
        val patternSymbol: PatternSymbol,
        val span: HTMLSpanElement,
    )

    private class Highlighter(
        private val items: Collection<MarkerItem>,
        private val capturingGroup: CapturingGroup
    ) {

        // highlight capturing group range in text
        private var isMarking = false
        val markIt: (Boolean, IntRange?) -> Unit = { selected, range ->
            items.forEach { item ->
                item.span.classList.toggle(
                    token = CLASS_HIGHLIGHT,
                    force = selected && (range == null || item.index in range)
                )
            }
        }
        val onMouseMoveFunction = { _: Event ->
            if (!isMarking) {
                markIt(true, capturingGroup.range)
                isMarking = true
            }
        }
        val onMouseOutFunction = { _: Event ->
            markIt(false, null)
            isMarking = false
        }
    }
}
