package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.js.Prism
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.CodeGeneratorOptions
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.MVCContract
import org.olafneumann.regex.generator.ui.components.LanguageAccordionCard
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

class P4LanguageDisplay(
    private val controller: MVCContract.Controller
) : NumberedPart(
    elementId = "rg_display_programming_languages",
    caption = "Usage in Programming Languages"
) {
    private val containerLanguages = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_DIV_LANGUAGES)

    private val checkCaseInsensitive = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_CASE_INSENSITIVE)
    private val checkDotAll = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_DOT_MATCHES_LINE_BRAKES)
    private val checkMultiline = HtmlHelper.getElementById<HTMLInputElement>(HtmlView.ID_CHECK_MULTILINE)

    private val languageDisplays = CodeGenerator.all
        .associateWith { LanguageAccordionCard(it, containerLanguages) }

    init {
        checkCaseInsensitive.oninput = { propagateOptionsChange() }
        checkDotAll.oninput = { propagateOptionsChange() }
        checkMultiline.oninput = { propagateOptionsChange() }
    }

    fun applyModel(model: DisplayModel) {
        this.options = model.codeGeneratorOptions
        val currentPattern = model.patternRecognizerModel.regularExpression.finalPattern

        CodeGenerator.all
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(currentPattern, model.codeGeneratorOptions)) }
        Prism.highlightAll()
    }

    private var options: CodeGeneratorOptions
        get() = CodeGeneratorOptions(
            caseInsensitive = checkCaseInsensitive.checked,
            dotMatchesLineBreaks = checkDotAll.checked,
            multiline = checkMultiline.checked
        )
        set(value) {
            checkCaseInsensitive.checked = value.caseInsensitive
            checkDotAll.checked = value.dotMatchesLineBreaks
            checkMultiline.checked = value.multiline
        }

    private fun propagateOptionsChange() {
        controller.onCodeGeneratorOptionsChange(options = options)
    }
}
