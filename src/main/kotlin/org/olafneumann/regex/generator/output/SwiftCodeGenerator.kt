package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache
import org.olafneumann.regex.generator.regex.RegexCombiner

internal class SwiftCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Swift",
    highlightLanguage = "swift",
    templateCode = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "%1${'$'}s"%2${'$'}s)
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
) {

    override fun transformPattern(pattern: String, options: RegexCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RegexCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = ".caseInsensitive",
            valueForDotAll = ".dotMatchesLineSeparators",
            valueForMultiline = ".anchorsMatchLines",
            separator = ", ",
            prefix = ", options: [",
            postfix = "]"
        )
}
