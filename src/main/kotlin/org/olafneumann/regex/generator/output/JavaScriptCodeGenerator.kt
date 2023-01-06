package org.olafneumann.regex.generator.output

class JavaScriptCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "JavaScript",
    highlightLanguage = "javascript",
    templateCode = """function useRegex(input) {
    let regex = /%1${'$'}s/%2${'$'}s;
    return regex.test(input);
}"""
) {

    override fun transformPattern(pattern: String): String =
        pattern.replace("/", "\\/").replace("\t", "\\t")

    override fun generateOptionsCode(options: CodeGeneratorOptions) =
        options.combine(valueForCaseInsensitive = "i", valueForMultiline = "m", valueForDotAll = "s")

    override fun getWarnings(pattern: String, options: CodeGeneratorOptions): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}
