package org.olafneumann.regex.generator.ui.parts

import org.olafneumann.regex.generator.js.Prism
import org.olafneumann.regex.generator.ui.model.DisplayModel
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.ui.HtmlView
import org.olafneumann.regex.generator.ui.components.LanguageAccordionCard
import org.olafneumann.regex.generator.ui.utils.HtmlHelper
import org.w3c.dom.HTMLDivElement

class P4LanguageDisplay {
    private val containerLanguages = HtmlHelper.getElementById<HTMLDivElement>(HtmlView.ID_DIV_LANGUAGES)

    private val languageDisplays = CodeGenerator.all
        .associateWith { LanguageAccordionCard(it, containerLanguages) }

    fun applyModel(model: DisplayModel) {
        val currentPattern = model.patternRecognizerModel.regularExpression.finalPattern
        val options = model.patternRecognizerModel.options

        CodeGenerator.all
            .forEach { languageDisplays[it]?.setSnippet(it.generateCode(currentPattern, options)) }
        Prism.highlightAll()
    }
}
