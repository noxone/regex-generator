package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.js.encodeURIComponent

interface CodeGenerator {
    companion object {
        val list = listOf<CodeGenerator>(
            JavaCodeGenerator()
            , KotlinCodeGenerator()
            , PhpCodeGenerator()
            , JavaScriptCodeGenerator()
            , CSharpCodeGenerator()
            , RubyCodeGenerator()
        ).sortedBy { it.languageName }
    }

    val languageName: String

    val highlightLanguage: String

    val uniqueName: String
        get() = languageName
            .replace("-", "_minus_")
            .replace("+", "_plus_")
            .replace("#", "_sharp_")

    fun generateCode(pattern: String, options: RecognizerCombiner.Options): GeneratedSnippet
}

data class GeneratedSnippet(
    val snippet: String,
    val warnings: List<String> = emptyList()
)

internal abstract class SimpleReplacingCodeGenerator : CodeGenerator {
    protected open fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String = pattern

    protected abstract val templateCode: String

    protected abstract fun generateOptionsCode(options: RecognizerCombiner.Options): String

    protected open fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> = emptyList()

    override fun generateCode(pattern: String, options: RecognizerCombiner.Options): GeneratedSnippet =
        GeneratedSnippet(
            templateCode.replace("%1${'$'}s", transformPattern(pattern, options))
                .replace("%2${'$'}s", generateOptionsCode(options)),
            getWarnings(pattern, options)
        )

    protected open fun combineOptions(
        options: RecognizerCombiner.Options,
        valueForCaseInsensitive: String? = null,
        valueForMultiline: String? = null,
        valueForDotAll: String? = null,
        valueIfNone: String = "",
        prefix: String = "",
        separator: String = "",
        postfix: String = "",
        mapper: (option: String) -> String = { s -> s }
    ): String {
        val optionList = mutableListOf<String>()
        if (options.caseSensitive && valueForCaseInsensitive != null)
            optionList += valueForCaseInsensitive
        if (options.dotMatchesLineBreaks && valueForDotAll != null)
            optionList += valueForDotAll
        if (options.multiline && valueForMultiline != null)
            optionList += valueForMultiline

        optionList.ifEmpty { return valueIfNone }
        return optionList.joinToString(separator = separator, prefix = prefix, postfix = postfix) { s -> mapper(s) }
    }
}

internal class Regex101CodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "Regex101"
    override val highlightLanguage: String
        get() = "regex101"

    override val templateCode: String
        get() = "https://regex101.com/?regex=%1\$s&flags=%2\$s"

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        encodeURIComponent(pattern)

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String =
        combineOptions(options,
            valueForCaseInsensitive = "i",
            valueForDotAll = "s",
            valueForMultiline = "m"
        )
}

internal class JavaCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String get() = "Java"
    override val highlightLanguage: String get() = "java"

    override val templateCode: String
        get() = """import java.util.regex.Pattern;
                |import java.util.regex.Matcher;
                |
                |public class Sample {
                |    public boolean useRegex(String input) {
                |        // Compile regular expression
                |        Pattern pattern = Pattern.compile("%1${'$'}s"%2${'$'}s);
                |        // Match regex against input
                |        Matcher matcher = pattern.matcher(input);
                |        // Use results...
                |        return matcher.matches();
                |    }
                |}""".trimMargin()

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\\"])"), "\\$1").replace(Regex("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = combineOptions(
        options,
        "CASE_INSENSITIVE",
        "MULTILINE",
        "DOTALL",
        prefix = " ,",
        separator = " | "
    ) { "Pattern.$it" }
}

internal class KotlinCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String get() = "Kotlin"
    override val highlightLanguage: String get() = "kotlin"

    override val templateCode: String
        get() = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "%1${'$'}s"%2${'$'}s)
    return regex.matches(input)
}"""

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\\"])"), "\\$1").replace(Regex("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = combineOptions(
        options,
        "IGNORE_CASE",
        "MULTILINE",
        "DOT_MATCHES_ALL",
        prefix = ", options = setOf(",
        postfix = ")",
        separator = ", "
    ) { "RegexOption.$it" }

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 'RegexOption.DOT_MATCHES_ALL' is only supported on JVM runtime.")
        return emptyList()
    }
}

internal class PhpCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "PHP"
    override val highlightLanguage: String
        get() = "php"

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\'])"), "\\$1").replace(Regex("\t"), "\\t")

    override val templateCode: String
        get() = """<?php
function useRegex(${'$'}input) {
    ${'$'}regex = '/%1${'$'}s/%2${'$'}s';
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")
}

internal class JavaScriptCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "JavaScript"
    override val highlightLanguage: String
        get() = "javascript"

    override val templateCode: String
        get() = """function useRegex(input) {
    let regex = /%1${'$'}s/%2${'$'}s;
    return regex.test(input);
}"""

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}

internal class CSharpCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "C#"
    override val highlightLanguage: String
        get() = "csharp"

    override val templateCode: String
        get() = """using System;
using System.Text.RegularExpressions;

public class Sample
{
    public static void useRegex(String input)
    {
        Regex regex = new Regex("%1${'$'}s"%2${'$'}s);
        return regex.IsMatch(input);
    }
}"""

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\\"])"), "\\$1").replace(Regex("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(
            options,
            "IgnoreCase",
            "Multiline",
            "Singleline",
            separator = " | ",
            prefix = ", "
        ) { "RegexOptions.$it" }
}


internal class RubyCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "Ruby"
    override val highlightLanguage: String
        get() = "ruby"

    override val templateCode: String
        get() = """def use_regex(input)
    regex = Regexp.new('%1${'$'}s'%2${'$'}s)
    regex.match input
end"""

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\'])"), "\\$1").replace(Regex("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(
            options,
            "IGNORECASE",
            null,
            "MULTILINE",
            separator = " | ",
            prefix = ", "
        ) { "Regexp::$it" }

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.multiline)
            return listOf("The Ruby regex engine does not support the MULTILINE option. Regex' are always treated as multiline.")
        return emptyList()
    }
}



