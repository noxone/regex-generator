package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RegexCache

interface CodeGenerator {
    companion object {
        val all = listOf<CodeGenerator>(
            JavaCodeGenerator()
            , KotlinCodeGenerator()
            , PhpCodeGenerator()
            , JavaScriptCodeGenerator()
            , CSharpCodeGenerator()
            , RubyCodeGenerator()
            , GrepCodeGenerator()
        ).sortedBy { it.languageName.lowercase() }
    }

    val languageName: String

    val highlightLanguage: String

    val uniqueName: String
        get() = languageName
            .replace("-", "_minus_")
            .replace("+", "_plus_")
            .replace("#", "_sharp_")

    fun generateCode(pattern: String, options: RecognizerCombiner.Options): GeneratedSnippet

    data class GeneratedSnippet(
        val snippet: String,
        val warnings: List<String> = emptyList()
    )
}

internal abstract class SimpleReplacingCodeGenerator(
    override val languageName: String,
    override val highlightLanguage: String,
    private val templateCode: String
) : CodeGenerator {
    protected open fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String = pattern

    protected abstract fun generateOptionsCode(options: RecognizerCombiner.Options): String

    protected open fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> = emptyList()

    override fun generateCode(pattern: String, options: RecognizerCombiner.Options): CodeGenerator.GeneratedSnippet =
        CodeGenerator.GeneratedSnippet(
            templateCode.replace("%1${'$'}s", transformPattern(pattern, options))
                .replace("%2${'$'}s", generateOptionsCode(options))
                .replace("%3${'$'}s", pattern),
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
        if (options.caseInsensitive && valueForCaseInsensitive != null)
            optionList += valueForCaseInsensitive
        if (options.dotMatchesLineBreaks && valueForDotAll != null)
            optionList += valueForDotAll
        if (options.multiline && valueForMultiline != null)
            optionList += valueForMultiline

        optionList.ifEmpty { return valueIfNone }
        return optionList.joinToString(separator = separator, prefix = prefix, postfix = postfix) { s -> mapper(s) }
    }
}

internal open class UrlGenerator(
    linkName: String,
    urlTemplate: String,
    private val valueForCaseInsensitive: String? = "i",
    private val valueForDotAll: String? = "s",
    private val valueForMultiline: String? = "m",
    private val additionCharactersToEscape: List<Char> = emptyList()
) : SimpleReplacingCodeGenerator(linkName, linkName, urlTemplate) {
    private fun String.escape(chars: List<Char>): String =
        if (chars.isEmpty()) this
        else replace(chars.first().toString(), "\\${chars.first()}").escape(chars.drop(1))

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        encodeURIComponent(pattern.escape(additionCharactersToEscape))

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String =
        combineOptions(options, valueForCaseInsensitive, valueForMultiline, valueForDotAll)
}

internal class JavaCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Java",
    highlightLanguage = "java",
    templateCode = """import java.util.regex.Pattern;
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
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = combineOptions(
        options,
        "CASE_INSENSITIVE",
        "MULTILINE",
        "DOTALL",
        prefix = " ,",
        separator = " | "
    ) { "Pattern.$it" }
}

internal class KotlinCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Kotlin",
    highlightLanguage = "kotlin",
    templateCode = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "%1${'$'}s"%2${'$'}s)
    return regex.matches(input)
}"""
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\$1").replace(RegexCache.get("\t"), "\\t")

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

internal class PhpCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "PHP",
    highlightLanguage = "php",
    templateCode = """<?php
function useRegex(${'$'}input) {
    ${'$'}regex = '/%1${'$'}s/%2${'$'}s';
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""
) {
    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern
            .replace(RegexCache.get("([\\\\'])"), "\\$1")
            .replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")
}

internal class JavaScriptCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "JavaScript",
    highlightLanguage = "javascript",
    templateCode = """function useRegex(input) {
    let regex = /%1${'$'}s/%2${'$'}s;
    return regex.test(input);
}"""
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace("\t", "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options, "i", "m", "s")

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("The option 's' (dot matches line breaks) is not supported in Firefox and IE.")
        return emptyList()
    }
}

internal class GrepCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "grep",
    highlightLanguage = "bash",
    templateCode = """grep -P%2${'$'}s '%1${'$'}s' [FILE...]"""
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace("'", "'\"'\"'")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(options = options, valueForCaseInsensitive = "-i", separator = " ", prefix = " ")

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        val messages = mutableListOf<String>()
        if (options.dotMatchesLineBreaks)
            messages.add("The option 's' (dot matches line breaks) is not supported for grep.")
        if (options.multiline)
            messages.add("The option 'm' (multiline) is not supported for grep.")
        messages.add("grep on mac OS does not support option -P (for Perl regex). " +
                "To make it work, install a better grep (e.g. brew install grep). Most regex will work without -P.")
        return messages
    }
}

internal class CSharpCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "C#",
    highlightLanguage = "csharp",
    templateCode = """using System;
using System.Text.RegularExpressions;

public class Sample
{
    public static void useRegex(String input)
    {
        Regex regex = new Regex("%1${'$'}s"%2${'$'}s);
        return regex.IsMatch(input);
    }
}"""
) {


    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\$1").replace(RegexCache.get("\t"), "\\t")

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

internal class RubyCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Ruby",
    highlightLanguage = "ruby",
    templateCode = """def use_regex(input)
    regex = Regexp.new('%1${'$'}s'%2${'$'}s)
    regex.match input
end"""
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\'])"), "\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        combineOptions(
            options,
            valueForCaseInsensitive = "IGNORECASE",
            valueForDotAll = "MULTILINE",
            separator = " | ",
            prefix = ", "
        ) { "Regexp::$it" }

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
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
