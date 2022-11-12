package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.regex.RegexCache

internal class KotlinCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Kotlin",
    highlightLanguage = "kotlin",
    templateCode = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "%1${'$'}s"%2${'$'}s)
    return regex.matches(input)
}"""
) {

    override fun transformPattern(pattern: String, options: Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: Options): String = options.combine(
        valueForCaseInsensitive = "RegexOption.IGNORE_CASE",
        valueForMultiline = "RegexOption.MULTILINE",
        valueForDotAll = "RegexOption.DOT_MATCHES_ALL",
        prefix = ", options = setOf(",
        postfix = ")",
        separator = ", "
    )

    override fun getWarnings(pattern: String, options: Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 'RegexOption.DOT_MATCHES_ALL' is only supported on JVM runtime.")
        return emptyList()
    }
}
