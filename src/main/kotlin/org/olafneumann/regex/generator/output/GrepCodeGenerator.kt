package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexMatchCombiner

internal class GrepCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "grep",
    highlightLanguage = "bash",
    templateCode = """grep -P%2${'$'}s '%1${'$'}s' [FILE...]"""
) {

    override fun transformPattern(pattern: String, options: RegexMatchCombiner.Options): String =
        pattern.replace("'", "'\"'\"'")

    override fun generateOptionsCode(options: RegexMatchCombiner.Options) =
        options.combine(valueForCaseInsensitive = "-i", separator = " ", prefix = " ")

    override fun getWarnings(pattern: String, options: RegexMatchCombiner.Options): List<String> {
        val messages = mutableListOf<String>()
        if (options.dotMatchesLineBreaks)
            messages.add("The option 's' (dot matches line breaks) is not supported for grep.")
        if (options.multiline)
            messages.add("The option 'm' (multiline) is not supported for grep.")
        messages.add(
            "grep on mac OS does not support option -P (for Perl regex). " +
                    "To make it work, install a better grep (e.g. brew install grep). Most regex will work without -P."
        )
        return messages
    }
}
