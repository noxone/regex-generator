package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.js.jQuery
import org.olafneumann.regex.generator.model.DisplayModel
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.olafneumann.regex.generator.ui.utils.TimerController
import org.olafneumann.regex.generator.ui.utils.UserInputDelayer
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement

class P1UserInput(
    private val controller: MVCContract.Controller,
    private val maxInputLength: Int,
    private val immediateUserInputAction: (String) -> Unit
) {
    private val textInput = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_INPUT_ELEMENT)
    private val divInputWarning = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_INPUT_MESSAGE_SHORTEN)

    private val inputWarningTimerController = TimerController()

    private var inputText: String
        get() = textInput.value
        set(value) {
            textInput.value = value
        }

    init {
        hideShortenWarning(withAnimation = false)

        val delayer = UserInputDelayer(
            immediateUserInputAction,
            { controller.setUserInput(it) }
        )
        textInput.oninput = { handleUserInput { delayer.onAction(it) } }
        textInput.maxLength = maxInputLength + 1
        HtmlHelper.getElementById<HTMLSpanElement>(HtmlView.ID_INPUT_MESSAGE_SHORTEN_NUMBER)
            .innerHTML = maxInputLength.toString()
    }

    fun showInputText(text: String) {
        inputText = text
    }

    private fun handleUserInput(action: (String) -> Unit) {
        val rawInputText = inputText
        val actualInputText = if (rawInputText.length <= maxInputLength) {
            rawInputText
        } else {
            showShortenWarning()
            this.inputText.substring(0, maxInputLength)
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