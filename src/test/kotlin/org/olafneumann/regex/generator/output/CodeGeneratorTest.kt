package org.olafneumann.regex.generator.output

import org.olafneumann.regex.generator.output.CodeGenerator.Companion.htmlIdCompatible
import org.olafneumann.regex.generator.regex.RegexCombiner
import org.olafneumann.regex.generator.regex.RegexCombiner.Options
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeGeneratorTest {
    companion object {
        // original text: abc.\$hier "und" / 'da'([).
        private const val given = "abc.\\\$hier \"und\" / 'da'([)."
    }

    private fun generateRegex(input: String): String =
        RegexCombiner.combineMatches(inputText = input, selectedMatches = emptyList(), options = Options()).pattern

    @Test
    fun testLanguageNameReplacement() {
        val given = ".abc_DEF-13 37#+xù\$x\\abc"
        val expected = "_dot_abc_DEF_minus_13__37_sharp__plus_x_u249__u36_x_u92_abc"

        val actual = given.htmlIdCompatible

        assertEquals(expected, actual)
    }

    @Test
    fun testGenerator_Regex() {
        val expected = """abc\.\\\${'$'}hier "und" / 'da'\(\[\)\."""

        val actual = generateRegex(given)

        assertEquals(expected, actual)
    }

    private fun testLanguageGenerator(codeGenerator: CodeGenerator, options: Options = Options(), expected: String) {
        val regex = generateRegex(given)
        val actual = codeGenerator.generateCode(pattern = regex, options = options).snippet
        assertEquals(expected, actual)
    }

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Java() = testLanguageGenerator(
        codeGenerator = JavaCodeGenerator(), expected = """import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Sample {
    public static boolean useRegex(final String input) {
        // Compile regular expression
        final Pattern pattern = Pattern.compile("abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", Pattern.CASE_INSENSITIVE);
        // Match regex against input
        final Matcher matcher = pattern.matcher(input);
        // Use results...
        return matcher.matches();
    }
}"""
    )

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Kotlin() = testLanguageGenerator(
        codeGenerator = KotlinCodeGenerator(), expected = """fun useRegex(input: String): Boolean {
    val regex = Regex(pattern = "abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
}"""
    )

    @Test
    fun testGenerator_C() = testLanguageGenerator(
        codeGenerator = CRegexCodeGenerator(), expected = """#include <regex.h>

int useRegex(char* textToCheck) {
    regex_t compiledRegex;
    int reti;
    int actualReturnValue = -1;
    char messageBuffer[100];

    /* Compile regular expression */
    reti = regcomp(&compiledRegex, "abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", REG_EXTENDED | REG_ICASE);
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
    )

    @Test
    fun testGenerator_CSharp() = testLanguageGenerator(
        codeGenerator = CSharpCodeGenerator(), expected = """using System;
using System.Text.RegularExpressions;

public class Sample
{
    public static bool useRegex(String input)
    {
        const Regex regex = new Regex("abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", RegexOptions.IgnoreCase);
        return regex.IsMatch(input);
    }
}"""
    )

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Grep() = testLanguageGenerator(
        codeGenerator = GrepCodeGenerator(), expected = """grep -P -i 'abc\.\\\${'$'}hier "und" / '"'"'da'"'"'\(\[\)\.' [FILE...]"""
    )

    @Test
    fun testGenerator_JavaScript() = testLanguageGenerator(
        codeGenerator = JavaScriptCodeGenerator(), expected = """function useRegex(input) {
    let regex = /abc\.\\\${'$'}hier "und" \/ 'da'\(\[\)\./i;
    return regex.test(input);
}"""
    )

    @Test
    fun testGenerator_PHP() = testLanguageGenerator(
        codeGenerator = PhpCodeGenerator(), expected = """<?php
function useRegex(${'$'}input) {
    ${'$'}regex = '/abc\\.\\\\\\${'$'}hier "und" / \'da\'\\(\\[\\)\\./i';
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""
    )

    @Test
    fun testGenerator_Ruby() = testLanguageGenerator(
        codeGenerator = RubyCodeGenerator(), expected = """def use_regex(input)
    regex = Regexp.new('abc\\.\\\\\\${'$'}hier "und" / \'da\'\\(\\[\\)\\.', Regexp::IGNORECASE)
    regex.match input
end"""
    )

    @Test
    fun testGenerator_Python() = testLanguageGenerator(
        codeGenerator = PythonCodeGenerator(), expected = """import re

def use_regex(input_text):
    pattern = re.compile(r"abc\.\\\${'$'}hier "'"'r"und"'"'r" / 'da'\(\[\)\.", re.IGNORECASE)
    return pattern.match(input_text)"""
    )

    private fun createPythonOutput(string: String) = """import re

def use_regex(input_text):
    pattern = re.compile(${string}, re.DOTALL)
    return pattern.match(input_text)"""

    private fun testPython(inputRegex: String, expectedString: String) {
        val regex = generateRegex(inputRegex)
        val expected = createPythonOutput(expectedString)

        val actual = PythonCodeGenerator()
            .generateCode(pattern = regex, options = Options(caseInsensitive = false, dotMatchesLineBreaks = true))
            .snippet

        assertEquals(expected, actual)
    }

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Python_withTrailingBackslash() =
        testPython(inputRegex = """given\\\""", expectedString = """r"given\\\\\\"""")

    @Test
    fun testGenerator_Python_quotationMarks1() =
        testPython(inputRegex = "'", expectedString = "r\"'\"")

    @Test
    fun testGenerator_Python_quotationMarks2() =
        testPython(inputRegex = "\"\"\"", expectedString = "r'\"\"\"'")

    @Test
    fun testGenerator_Python_quotationMarks3() =
        testPython(inputRegex = "\"'", expectedString = "'\"'r\"'\"")

    @Test
    fun testGenerator_Python_quotationMarks4() =
        testPython(inputRegex = "'\"", expectedString = "r\"'\"'\"'")

    @Test
    fun testGenerator_Python_quotationMarks5() =
        testPython(inputRegex = "\"\"\"\"\"'\"\"", expectedString = "'\"\"\"\"\"'r\"'\"'\"\"'")

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Swift() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = true), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", options: [.caseInsensitive])
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
    )

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Swift_withoutOptions() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = false), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.")
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
    )

    @Suppress("MaxLineLength")
    @Test
    fun testGenerator_Swift_withAllOptions() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = true, multiline = true, dotMatchesLineBreaks = true), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" / 'da'\\(\\[\\)\\.", options: [.caseInsensitive, .dotMatchesLineSeparators, .anchorsMatchLines])
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
    )

    @Test
    fun testGenerator_VisualBasicNet() = testLanguageGenerator(
        codeGenerator = VisualBasicNetCodeGenerator(), expected = """Imports System.Text.RegularExpressions

Public Module Sample
    Public Function useRegex(ByVal input As String) As Boolean
        Dim regex = New Regex("abc\.\\\${'$'}hier ""und"" / 'da'\(\[\)\.", RegexOptions.IgnoreCase)
        Return regex.IsMatch(input)
    End Function
End Module"""
    )

    @Suppress("MaxLineLength")
    @Test
    fun testGenerator_PatternUrlGenerator() = testLanguageGenerator(
            codeGenerator = UrlGenerator(
                linkName = "Regex101",
                urlTemplate = "https://regex101.com/?regex=%1\$s&flags=g%2\$s&delimiter=/"
            ),
            expected = "https://regex101.com/?regex=abc%5C.%5C%5C%5C%24hier%20%22und%22%20%2F%20'da'%5C(%5C%5B%5C)%5C.&flags=gi&delimiter=/"
    )
}
