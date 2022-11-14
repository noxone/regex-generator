package org.olafneumann.regex.generator.output;

import org.olafneumann.regex.generator.regex.Options

class JavaScriptCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "JavaScript",
    highlightLanguage = "javascript",
    templateCode = """function useRegex(input) {
    let regex = /%1${'$'}s/%2${'$'}s;
    return regex.test(input);
}"""
) {

    override fun transformPattern(pattern: String, options: Options): String =
        pattern.replace("/", "\\/").replace("\t", "\\t")

    override fun generateOptionsCode(options: Options) =
        options.combine(valueForCaseInsensitive = "i", valueForMultiline = "m", valueForDotAll = "s")

    override fun getWarnings(pattern: String, options: Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}
