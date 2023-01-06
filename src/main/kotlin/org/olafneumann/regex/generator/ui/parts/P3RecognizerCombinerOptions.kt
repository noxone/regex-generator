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
    caption = "Options",
    initialStateOpen = false,
) {
    private val checkOnlyMatches = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_WHOLELINE)
    private val checkGenerateLowerCase =
        HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_GENERATE_LOWER_CASE)

    init {
        checkOnlyMatches.oninput = { propagateOptionsChange() }
        checkWholeLine.oninput = { propagateOptionsChange() }
        checkGenerateLowerCase.oninput = { propagateOptionsChange() }
    }

    private var options: RecognizerMatchCombinerOptions
        get() = RecognizerMatchCombinerOptions(
            onlyPatterns = checkOnlyMatches.checked,
            matchWholeLine = checkWholeLine.checked,
            generateLowerCase = checkGenerateLowerCase.checked,
        )
        set(value) {
            checkOnlyMatches.checked = value.onlyPatterns
            checkWholeLine.checked = value.matchWholeLine
            checkGenerateLowerCase.checked = value.generateLowerCase
        }

    fun applyModel(displayModel: DisplayModel) {
        this.options = displayModel.patternRecognizerModel.recognizerMatchCombinerOptions
    }

    private fun propagateOptionsChange() {
        controller.onRecognizerCombinerOptionsChange(options = options)
    }
}
