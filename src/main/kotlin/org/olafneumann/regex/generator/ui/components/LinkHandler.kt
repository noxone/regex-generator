package org.olafneumann.regex.generator.ui.components

import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.regex.Options
import org.w3c.dom.HTMLAnchorElement

internal class LinkHandler(
    private val link: HTMLAnchorElement,
    private val codeGenerator: CodeGenerator
) {
    fun setPattern(pattern: String, options: Options) {
        link.href = codeGenerator.generateCode(pattern, options).snippet
    }
}
