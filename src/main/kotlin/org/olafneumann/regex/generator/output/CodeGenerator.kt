package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.js.encodeURIComponent
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RegexCache

interface CodeGenerator {
    companion object {
        val all = listOf<CodeGenerator>(
            CRegexCodeGenerator()
            , CSharpCodeGenerator()
            , GrepCodeGenerator()
            , JavaCodeGenerator()
            , JavaScriptCodeGenerator()
            , KotlinCodeGenerator()
            , PhpCodeGenerator()
            , PythonCodeGenerator()
            , RubyCodeGenerator()
            , SwiftCodeGenerator()
            , VisualBasicNetCodeGenerator()
        ).sortedBy { it.languageName.lowercase() }

        private val String.codePointString: String
            get() {
                return if (isNotEmpty()) {
                    "_u${this[0].code}_"
                } else {
                    ""
                }
            }

        internal val String.htmlIdCompatible: String
            get() = this
                .replace(" ", "__")
                .replace("-", "_minus_")
                .replace(".", "_dot_")
                .replace("+", "_plus_")
                .replace("#", "_sharp_")
                .replace(regex = Regex("[^_A-Za-z0-9]"), transform = { it.value.codePointString })
    }

    val languageName: String

    val highlightLanguage: String

    val uniqueName: String
        get() = languageName.htmlIdCompatible

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

    @Suppress("LongParameterList")
    protected fun RecognizerCombiner.Options.combine(
        valueForCaseInsensitive: String? = null,
        valueForMultiline: String? = null,
        valueForDotAll: String? = null,
        prefix: String = "",
        separator: String = "",
        postfix: String = ""
    ): String {
        val optionList = mutableListOf<String>()
        if (caseInsensitive && valueForCaseInsensitive != null)
            optionList += valueForCaseInsensitive
        if (dotMatchesLineBreaks && valueForDotAll != null)
            optionList += valueForDotAll
        if (multiline && valueForMultiline != null)
            optionList += valueForMultiline

        return if (optionList.isNotEmpty()) {
            optionList.joinToString(separator = separator, prefix = prefix, postfix = postfix)
        } else {
            ""
        }
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
        options.combine(
            valueForCaseInsensitive = valueForCaseInsensitive,
            valueForMultiline = valueForMultiline,
            valueForDotAll = valueForDotAll
        )
}

internal class JavaCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Java",
    highlightLanguage = "java",
    templateCode = """import java.util.regex.Pattern;
                |import java.util.regex.Matcher;
                |
                |public class Sample {
                |    public static boolean useRegex(final String input) {
                |        // Compile regular expression
                |        final Pattern pattern = Pattern.compile("%1${'$'}s"%2${'$'}s);
                |        // Match regex against input
                |        final Matcher matcher = pattern.matcher(input);
                |        // Use results...
                |        return matcher.matches();
                |    }
                |}""".trimMargin()
) {

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = options.combine(
        valueForCaseInsensitive = "Pattern.CASE_INSENSITIVE",
        valueForMultiline = "Pattern.MULTILINE",
        valueForDotAll = "Pattern.DOTALL",
        prefix = ", ",
        separator = " | "
    )
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
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options): String = options.combine(
        valueForCaseInsensitive = "RegexOption.IGNORE_CASE",
        valueForMultiline = "RegexOption.MULTILINE",
        valueForDotAll = "RegexOption.DOT_MATCHES_ALL",
        prefix = ", options = setOf(",
        postfix = ")",
        separator = ", "
    )

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
            .replace(RegexCache.get("([\\\\'])"), "\\\\$1")
            .replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(valueForCaseInsensitive = "i", valueForMultiline = "m", valueForDotAll = "s")
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
        pattern.replace("/", "\\/").replace("\t", "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(valueForCaseInsensitive = "i", valueForMultiline = "m", valueForDotAll = "s")

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
        options.combine(valueForCaseInsensitive = "-i", separator = " ", prefix = " ")

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
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

internal class CSharpCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "C#",
    highlightLanguage = "csharp",
    templateCode = """using System;
using System.Text.RegularExpressions;

public class Sample
{
    public static bool useRegex(String input)
    {
        const Regex regex = new Regex("%1${'$'}s"%2${'$'}s);
        return regex.IsMatch(input);
    }
}"""
) {


    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "RegexOptions.IgnoreCase",
            valueForMultiline = "RegexOptions.Multiline",
            valueForDotAll = "RegexOptions.Singleline",
            separator = " | ",
            prefix = ", "
        )
}

internal class CRegexCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "C (regex.h)",
    highlightLanguage = "c",
    templateCode = """#include <regex.h>

int useRegex(char* textToCheck) {
    regex_t compiledRegex;
    int reti;
    int actualReturnValue = -1;
    char messageBuffer[100];

    /* Compile regular expression */
    reti = regcomp(&compiledRegex, "%1${'$'}s", REG_EXTENDED%2${'$'}s);
    if (reti) {
        fprintf(stderr, "Could not compile regex\n");
        return -2;
    }

    /* Execute compiled regular expression */
    reti = regexec(&compiledRegex, textToCheck, 0, NULL, 0);
    if (!reti) {
        puts("Match");
        actualReturnValue = 0;
    } else if (reti == REG_NOMATCH) {
        puts("No match");
        actualReturnValue = 1;
    } else {
        regerror(reti, &compiledRegex, messageBuffer, sizeof(messageBuffer));
        fprintf(stderr, "Regex match failed: %s\n", messageBuffer);
        actualReturnValue = -3;
    }

    /* Free memory allocated to the pattern buffer by regcomp() */
    regfree(&compiledRegex);
    return actualReturnValue;
}"""
) {


    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "REG_ICASE",
            valueForMultiline = "REG_NEWLINE",
            separator = " | ",
            prefix = " | "
        )

    override fun getWarnings(pattern: String, options: RecognizerCombiner.Options): List<String> {
        if (options.dotMatchesLineBreaks)
            return listOf("DOT_ALL is not supported by regex.h")

        return emptyList()
    }
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
        pattern.replace(RegexCache.get("([\\\\'])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "Regexp::IGNORECASE",
            valueForDotAll = "Regexp::MULTILINE",
            separator = " | ",
            prefix = ", "
        )

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

    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("([\\\\\"])"), "\\\\$1").replace(RegexCache.get("\t"), "\\t")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = ".caseInsensitive",
            valueForDotAll = ".dotMatchesLineSeparators",
            valueForMultiline = ".anchorsMatchLines",
            separator = ", ",
            prefix = ", options: [",
            postfix = "]"
        )
}

internal class PythonCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Python",
    highlightLanguage = "python",
    templateCode = """import re

def use_regex(input_text):
    pattern = re.compile(r"%1${'$'}s%2${'$'}s)
    return pattern.match(input_text)"""
) {
    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String {
        return (
                pattern
                    // escape quotation mark through backslashes
                    .replace(RegexCache.get("(\")"), "\\\\\"")
                    // handle backslash at end of string (but this might add an empty r-string)
                    .replace(RegexCache.get("""(\\+)$"""), "\"'$1$1'r\"")
                // add trailing quotation mark because it is not available in the template!
                + '"'
            )
            // remove unnecessary empty r-string at end of sequence
            .replace(RegexCache.get("r\"\"$"), "")
    }

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "re.IGNORECASE",
            valueForDotAll = "re.DOTALL",
            valueForMultiline = "re.MULTILINE",
            separator = ", ",
            prefix = ", "
        )
}

internal class VisualBasicNetCodeGenerator : SimpleReplacingCodeGenerator(
    languageName = "Visual Basic .NET",
    highlightLanguage = "vbnet",
    templateCode = """Imports System.Text.RegularExpressions

Public Module Sample
    Public Function useRegex(ByVal input As String) As Boolean
        Dim regex = New Regex("%1${'$'}s"%2${'$'}s)
        Return regex.IsMatch(input)
    End Function
End Module"""
) {


    override fun transformPattern(pattern: String, options: RecognizerCombiner.Options): String =
        pattern.replace(RegexCache.get("\""), "\"\"")

    override fun generateOptionsCode(options: RecognizerCombiner.Options) =
        options.combine(
            valueForCaseInsensitive = "RegexOptions.IgnoreCase",
            valueForMultiline = "RegexOptions.Multiline",
            valueForDotAll = "RegexOptions.Singleline",
            separator = " Or ",
            prefix = ", "
        )
}
