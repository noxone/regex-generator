package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.regex.RegexCache

class PhpCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "PHP",
    highlightLanguage = "php",
    templateCode = """<?php
function useRegex(${'$'}input) {
    ${'$'}regex = '/%1${'$'}s/%2${'$'}s';
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""
) {
    override fun transformPattern(pattern: String): String =
        pattern
            .replace(RegexCache.get("([\\\\'])"), "\\\\$1")
            .replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: CodeGeneratorOptions) =
        options.combine(valueForCaseInsensitive = "i", valueForMultiline = "m", valueForDotAll = "s")
}
