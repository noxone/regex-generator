package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache

class GoCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Go",
    highlightLanguage = "go",
    templateCode = """package main

import (
	"regexp"
)

func useRegex(s string) bool {
	re := regexp.MustCompile("%2${'$'}s%1${'$'}s")
	return re.MatchString(s)
}"""
) {

    override fun transformPattern(pattern: String): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: CodeGeneratorOptions) =
        options.combine(
            valueForCaseInsensitive = "i",
            valueForDotAll = "s",
            valueForMultiline = "m",
            separator = "",
            prefix = "(?",
            postfix = ")"
        )
}
