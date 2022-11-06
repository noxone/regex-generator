package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.regex.RegexCombiner
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLInputElement

class PXOptions(
    private val controller: MVCContract.Controller
) {
    private val checkOnlyMatches = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_ONLY_MATCHES)
    private val checkWholeLine = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_WHOLELINE)
    private val checkCaseInsensitive = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_MULTILINE)

    init {
        checkCaseInsensitive.oninput = { propagateOptionsChange() }
        checkDotAll.oninput = { propagateOptionsChange() }
        checkMultiline.oninput = { propagateOptionsChange() }
        checkOnlyMatches.oninput = { propagateOptionsChange() }
        checkWholeLine.oninput = { propagateOptionsChange() }
    }

    private var options: RegexCombiner.Options
        get() = RegexCombiner.Options(
            onlyPatterns = checkOnlyMatches.checked,
            matchWholeLine = checkWholeLine.checked,
            caseInsensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )
        set(value) {
            checkOnlyMatches.checked = value.onlyPatterns
            checkWholeLine.checked = value.matchWholeLine
            checkCaseInsensitive.checked = value.caseInsensitive
            checkDotAll.checked = value.dotMatchesLineBreaks
            checkMultiline.checked = value.multiline
        }

    fun applyModel(displayModel: DisplayModel) {
        this.options = displayModel.patternRecognizerModel.options
    }

    private fun propagateOptionsChange() {
        controller.onOptionsChange(options = options)
    }
}
