package org.olafneumann.regex.generator.regex

interface CodeGenerator {
    companion object {
        val list = listOf<CodeGenerator>(
            JavaCodeGenerator()
            , KotlinCodeGenerator()
            , PhpCodeGenerator()
            , JavascriptCodeGenerator()
            , CSharpCodeGenerator()
            //,PythonCodeGenerator()
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
    protected abstract fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String

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

internal abstract class CLikeCodeGenerator : SimpleReplacingCodeGenerator() {
    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\\"])"), "\\$1")
}

internal class JavaCodeGenerator : CLikeCodeGenerator() {
    override val languageName: String get() = "Java"
    override val highlightLanguage: String get() = "java"

    override val templateCode: String
        get() = """import java.util.regex.Pattern;
                |import java.util.regex.Matcher;
                |
                |public class Sample {
                |    public boolean useRegex(String input) {
                |        // Compile regular expression
                |        Pattern p = Pattern.compile("%1${'$'}s"%2${'$'}s);
                |        // Match regex against input
                |        Matcher m = p.matcher(input);
                |        // Use results...
                |        return m.matches();
                |    }
                |}""".trimMargin()

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = combineOptions(
        options,
        "CASE_INSENSITIVE",
        "MULTILINE",
        "DOTALL",
        prefix = " ,",
        separator = " | "
    ) { s -> "Pattern.$s" }
}

internal class KotlinCodeGenerator : CLikeCodeGenerator() {
    override val languageName: String get() = "Kotlin"
    override val highlightLanguage: String get() = "kotlin"

    override val templateCode: String
        get() = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "%1${'$'}s"%2${'$'}s)
    return regex.matches(input)
}"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = combineOptions(
        options,
        "IGNORE_CASE",
        "MULTILINE",
        "DOT_MATCHES_ALL",
        prefix = ", options = setOf(",
        postfix = ")",
        separator = ", "
    ) { s -> "RegexOption.$s" }

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
        pattern.replace(Regex("([\\\\\"'])"), "\\$1")

    override val templateCode: String
        get() = """<?php
function useRegex(${'$'}input)
{
    ${'$'}regex = "/%1${'$'}s/%2${'$'}s";
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")
}

internal class JavascriptCodeGenerator : CLikeCodeGenerator() {
    override val languageName: String
        get() = "JavaScript"
    override val highlightLanguage: String
        get() = "javascript"

    override val templateCode: String
        get() = """function useRegex(input) {
    let regex = /%1${'$'}s/%2${'$'}s;
    return regex.test(input);
}"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}

internal class CSharpCodeGenerator : CLikeCodeGenerator() {
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

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(
            options,
            "IgnoreCase",
            "Multiline",
            "Singleline",
            separator = " | ",
            prefix = ", "
        ) { s -> "RegexOptions.$s" }

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}

internal class PythonCodeGenerator : SimpleReplacingCodeGenerator() {
    override val languageName: String
        get() = "Python"
    override val highlightLanguage: String
        get() = "python"

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(Regex("([\\\\'])"), "\\$1")

    override val templateCode: String
        get() = """import re

pattern = '%1${'$'}s'
test_string = 'This is a test.'
result = re.match(pattern, test_string, flags = %2${'$'}s)

if result:
  print("Search successful.")
else:
  print("Search unsuccessful.")	"""

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "I", "M", "S", valueIfNone = "0", separator = " | ") { s -> "re.$s" }
}



