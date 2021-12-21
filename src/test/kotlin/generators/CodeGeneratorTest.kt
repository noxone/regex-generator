package generators

import org.olafneumann.regex.generator.output.CSharpCodeGenerator
import org.olafneumann.regex.generator.output.CodeGenerator
import org.olafneumann.regex.generator.output.GrepCodeGenerator
import org.olafneumann.regex.generator.output.JavaCodeGenerator
import org.olafneumann.regex.generator.output.JavaScriptCodeGenerator
import org.olafneumann.regex.generator.output.KotlinCodeGenerator
import org.olafneumann.regex.generator.output.PhpCodeGenerator
import org.olafneumann.regex.generator.output.RubyCodeGenerator
import org.olafneumann.regex.generator.output.SwiftCodeGenerator
import org.olafneumann.regex.generator.output.VisualBasicNetCodeGenerator
import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerCombiner.Options
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeGeneratorTest {
    companion object {
        // original text: abc.\$hier "und" / 'da'([).
        private const val given = "abc.\\\$hier \"und\" / 'da'([)."
    }

    private fun generateRegex(input: String): String =
        RecognizerCombiner.combineMatches(inputText = input, selectedMatches = emptyList(), options = Options()).pattern

    @Test
    fun testGenerator_Regex() {
        val expected = """abc\.\\\${'$'}hier "und" da\(\[\)\."""

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
        final Pattern pattern = Pattern.compile("abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.", Pattern.CASE_INSENSITIVE);
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
    val regex = Regex(pattern = "abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.", options = setOf(RegexOption.IGNORE_CASE))
    return regex.matches(input)
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
        const Regex regex = new Regex("abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.", RegexOptions.IgnoreCase);
        return regex.IsMatch(input);
    }
}"""
    )

    @Test
    fun testGenerator_Grep() = testLanguageGenerator(
        codeGenerator = GrepCodeGenerator(), expected = """grep -P -i 'abc\.\\\${'$'}hier "und" da\(\[\)\.' [FILE...]"""
    )

    @Test
    fun testGenerator_JavaScript() = testLanguageGenerator(
        codeGenerator = JavaScriptCodeGenerator(), expected = """function useRegex(input) {
    let regex = /abc\.\\\${'$'}hier "und" da\(\[\)\./i;
    return regex.test(input);
}"""
    )

    @Test
    fun testGenerator_PHP() = testLanguageGenerator(
        codeGenerator = PhpCodeGenerator(), expected = """<?php
function useRegex(${'$'}input) {
    ${'$'}regex = '/abc\\.\\\\\\${'$'}hier "und" da\\(\\[\\)\\./i';
    return preg_match(${'$'}regex, ${'$'}input);
}
?>"""
    )

    @Test
    fun testGenerator_Ruby() = testLanguageGenerator(
        codeGenerator = RubyCodeGenerator(), expected = """def use_regex(input)
    regex = Regexp.new('abc\\.\\\\\\${'$'}hier "und" da\\(\\[\\)\\.', Regexp::IGNORECASE)
    regex.match input
end"""
    )

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Swift() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = true), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.", options: [.caseInsensitive])
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
    )

    @Test
    @Suppress("MaxLineLength")
    fun testGenerator_Swift_withoutOptions() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = false), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.")
    let range = NSRange(location: 0, length: text.count)
    let matches = regex.matches(in: text, options: [], range: range)
    return matches.first != nil
}"""
    )

    @Suppress("MaxLineLength")
    @Test
    fun testGenerator_Swift_withAllOptions() = testLanguageGenerator(
        codeGenerator = SwiftCodeGenerator(), options = Options(caseInsensitive = true, multiline = true, dotMatchesLineBreaks = true), expected = """func useRegex(for text: String) -> Bool {
    let regex = try! NSRegularExpression(pattern: "abc\\.\\\\\\${'$'}hier \"und\" da\\(\\[\\)\\.", options: [.caseInsensitive, .dotMatchesLineSeparators, .anchorsMatchLines])
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
        Dim regex = New Regex("abc\.\\\${'$'}hier ""und"" / 'da'\(\[\)\.", RegexOptions.IgnoreCase")
        Return regex.IsMatch(input)
    End Function
End Module"""
    )

}
