package org.olafneumann.regex.generator.ui.parts

import kotlinx.html.ButtonType
import kotlinx.html.button
import kotlinx.html.i
import kotlinx.html.id
import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.TimerController
import org.olafneumann.regex.generator.ui.utils.UserInputDelayer
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement

class P1UserInput(
    private val controller: MVCContract.Controller,
    private val maxInputLength: Int,
    immediateUserInputAction: (String) -> Unit
) : AbstractPart(
    elementId = "rg_input_container",
    caption = "Paste a sample text.",
    rightCaptionElement = {
        button(classes = "btn btn-link btn-sm", type = ButtonType.button) {
            id = "rg_button_show_help"
            i(classes = "bi bi-question-circle")
        }
    }
) {
    private val textInput = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_INPUT_ELEMENT)
    private val divInputWarning = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_INPUT_MESSAGE_SHORTEN)

    private val inputWarningTimerController = TimerController()

    private val shortenTextRegex = Regex("^(.{0,$maxInputLength})")

    private var inputText: String
        get() = textInput.value
        set(value) {
            textInput.value = value
        }

    init {
        hideShortenWarning(withAnimation = false)

        val delayer = UserInputDelayer(
            immediateUserInputAction,
            { controller.onUserInputChange(it) }
        )
        textInput.oninput = { handleUserInput { delayer.onAction(it) } }
        textInput.maxLength = maxInputLength + 1
        HtmlHelper.getElementById<HTMLSpanElement>(HtmlView.ID_INPUT_MESSAGE_SHORTEN_NUMBER)
            .innerHTML = maxInputLength.toString()
    }

    fun applyModel(model: DisplayModel) {
        inputText = model.patternRecognizerModel.input
    }

    private fun handleUserInput(action: (String) -> Unit) {
        val rawInputText = inputText
        val actualInputText = if (rawInputText.length <= maxInputLength) {
            rawInputText
        } else {
            showShortenWarning()
            shortenTextRegex.find(rawInputText)
                ?.groupValues
                ?.last()
                ?: rawInputText
        }
        inputText = actualInputText
        action(actualInputText)
    }

    private fun showShortenWarning() {
        jQuery(divInputWarning).slideDown()
        inputWarningTimerController.setTimeout({ hideShortenWarning(withAnimation = true) }, HtmlView.HIDE_DELAY)
    }

    private fun hideShortenWarning(withAnimation: Boolean) {
        if (withAnimation) {
            jQuery(divInputWarning).slideUp()
        } else {
            jQuery(divInputWarning).hide()
        }
    }
}
