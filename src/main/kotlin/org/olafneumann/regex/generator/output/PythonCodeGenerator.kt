package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache

class PythonCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Python",
    highlightLanguage = "python",
    templateCode = """import re

def use_regex(input_text):
    pattern = re.compile(%1${'$'}s%2${'$'}s)
    return pattern.match(input_text)"""
) {
    override fun transformPattern(pattern: String): String =
        if (pattern.contains('"') && !pattern.contains('\'')) {
            transformPatternWithQuotationMarks(pattern, quotationMark = '\'', other = '"')
        } else {
            transformPatternWithQuotationMarks(pattern, quotationMark = '"', other = '\'')
        }

    private fun transformPatternWithQuotationMarks(pattern: String, quotationMark: Char, other: Char): String {
        val multipleQuotationMarks = "(${quotationMark}+)"
        val multipleQuotationMarkReplacement = "${quotationMark}${other}$1${other}r${quotationMark}"
        val oddBackslashesReplacement = "${quotationMark}${other}$1$1${other}r${quotationMark}"
        val escapedPattern = pattern
            // escape quotation mark through string concatenation
            .replace(RegexCache.get(multipleQuotationMarks), multipleQuotationMarkReplacement)
            // handle odd number of backslashes at end of string
            .replace(RegexCache.get("""[^\\](?:\\\\)*(\\)${'$'}"""), oddBackslashesReplacement)

        // The template does not contain quotation marks, because they depend on the pattern content.
        // By default, this generator uses r-string (which have nearly no escape sequences).
        return "r${quotationMark}${escapedPattern}${quotationMark}"
            // The previous backslash handling in some rare cases might introduce empty r-strings at the beginning
            // or end of the string. To generate more pretty code these empty r-strings will be removed.
            .replace(RegexCache.get("^r${quotationMark}${quotationMark}|r${quotationMark}${quotationMark}$"), "")
    }

    override fun generateOptionsCode(options: CodeGeneratorOptions) =
        options.combine(
            valueForCaseInsensitive = "re.IGNORECASE",
            valueForDotAll = "re.DOTALL",
            valueForMultiline = "re.MULTILINE",
            separator = ", ",
            prefix = ", "
        )
}
