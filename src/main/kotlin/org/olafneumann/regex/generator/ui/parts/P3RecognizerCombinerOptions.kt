package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.regex.RecognizerMatchCombinerOptions
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLInputElement

class P3RecognizerCombinerOptions(
    private val controller: MVCContract.Controller
) : NumberedExpandablePart(
    "rg_options",
    caption = "Options"
) {
    private val checkOnlyMatches = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_WHOLELINE)

    init {
        checkOnlyMatches.oninput = { propagateOptionsChange() }
        checkWholeLine.oninput = { propagateOptionsChange() }
    }

    private var options: RecognizerMatchCombinerOptions
        get() = RecognizerMatchCombinerOptions(
            onlyPatterns = checkOnlyMatches.checked,
            matchWholeLine = checkWholeLine.checked,
        )
        set(value) {
            checkOnlyMatches.checked = value.onlyPatterns
            checkWholeLine.checked = value.matchWholeLine
        }

    fun applyModel(displayModel: DisplayModel) {
        console.log(displayModel.patternRecognizerModel.recognizerMatchCombinerOptions)
        this.options = displayModel.patternRecognizerModel.recognizerMatchCombinerOptions
    }

    private fun propagateOptionsChange() {
        controller.onRecognizerCombinerOptionsChange(options = options)
    }
}