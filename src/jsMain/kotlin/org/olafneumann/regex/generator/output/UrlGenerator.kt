package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.js.encodeURIComponent

open class UrlGenerator(
    linkName: String,
    urlTemplate: String,
    private val valueForCaseInsensitive: String? = "i",
    private val valueForDotAll: String? = "s",
    private val valueForMultiline: String? = "m",
) : SimpleReplacingCodeGenerator(linkName, linkName, urlTemplate) {
    override fun transformPattern(pattern: String): String =
        encodeURIComponent(pattern)

    override fun generateOptionsCode(options: CodeGeneratorOptions): String =
        options.combine(
            valueForCaseInsensitive = valueForCaseInsensitive,
            valueForMultiline = valueForMultiline,
            valueForDotAll = valueForDotAll
        )
}
