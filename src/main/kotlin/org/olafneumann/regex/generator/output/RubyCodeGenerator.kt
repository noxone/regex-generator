package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.regex.RegexCache

internal class RubyCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Ruby",
    highlightLanguage = "ruby",
    templateCode = """def use_regex(input)
    regex = Regexp.new('%1${'$'}s'%2${'$'}s)
    regex.match input
end"""
) {

    override fun transformPattern(pattern: String, options: Options): String =
        pattern.replace(RegexCache.get("([\\\\'])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: Options) =
        options.combine(
            valueForCaseInsensitive = "Regexp::IGNORECASE",
            valueForDotAll = "Regexp::MULTILINE",
            separator = " | ",
            prefix = ", "
        )

    override fun getWarnings(pattern: String, options: Options): List<String> {
        val warnings = mutableListOf<String>()
        if (options.multiline)
            warnings += "The Ruby regex engine does not support the MULTILINE option. " +
                    "Regex' are always treated as multiline."
        if (options.dotMatchesLineBreaks)
        // https://riptutorial.com/regex/example/18156/dotall-modifier
            warnings += "In Ruby, the DOTALL modifier equivalent is m, Regexp::MULTILINE modifier."

        return warnings
    }
}
