package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.regex.RecognizersTestHelper.assertMatchesEqual
import kotlin.test.Test

class BracketedRecognizerTest {
    @Test
    fun testBracketedRecognizer_Parenthesis() {
        val given = "ab(c123)def4),(5ghi"
        val recognizer = BracketedRecognizer(
            name = "Parentheses",
            startPattern = "\\(",
            endPattern = "\\)",
            centerPatterns = listOf(
                BracketedRecognizer.CenterPattern("no parentheses", "[^)]*")
            ),
        )

        val result = recognizer.findMatches(given)

        val no1 = RecognizerMatch(
            patterns = listOf("\\(", "\\)"),
            ranges = listOf(IntRange(2, 2), IntRange(7, 7)),
            recognizer = recognizer,
            title = "Parentheses",
            priority = 0
        )
        val no2 = RecognizerMatch(
            patterns = listOf("[^)]*"),
            ranges = listOf(IntRange(3, 6)),
            recognizer = recognizer,
            title = "Parentheses content (no parentheses)",
            priority = 0
        )
        assertMatchesEqual(expected = listOf(no1, no2), actual = result)
    }

    @Test
    fun testBracketedRecognizer_QuotationMark() {
        val given = "ab\"c123\\\"def4\",5ghi"
        val recognizer = BracketedRecognizer(
            name = "String (quotation mark)",
            startPattern = "\"",
            endPattern = "\"",
            centerPatterns = listOf(
                BracketedRecognizer.CenterPattern("no quotation mark", "[^\"]*"),
                BracketedRecognizer.CenterPattern("backslash escaped quotation mark", """(?:[^\\"]|\\\\|\\")*"""),
                BracketedRecognizer.CenterPattern("double escaped quotation mark", """(?:[^"]|"")*""")
            )
        )

        val result = recognizer.findMatches(given)

        val no1 = RecognizerMatch(
            patterns = listOf("\"", "\""),
            ranges = listOf(IntRange(2, 2), IntRange(8, 8)),
            recognizer = recognizer,
            title = "String (quotation mark)"
        )
        val no2 = RecognizerMatch(
            patterns = listOf("[^\"]*"),
            ranges = listOf(IntRange(3, 7)),
            recognizer = recognizer,
            title = "String (quotation mark) content (no quotation mark)"
        )
        val no3 = RecognizerMatch(
            patterns = listOf("\"", "\""),
            ranges = listOf(IntRange(2, 2), IntRange(13, 13)),
            recognizer = recognizer,
            title = "String (quotation mark)"
        )
        val no4 = RecognizerMatch(
            patterns = listOf("(?:[^\\\\\"]|\\\\\\\\|\\\\\")*"),
            ranges = listOf(IntRange(3, 12)),
            recognizer = recognizer,
            title = "String (quotation mark) content (backslash escaped quotation mark)"
        )
        val no5 = RecognizerMatch(
            patterns = listOf("(?:[^\"]|\"\")*"),
            ranges = listOf(IntRange(3, 7)),
            recognizer = recognizer,
            title = "String (quotation mark) content (double escaped quotation mark)"
        )
        val no6 = RecognizerMatch(
            patterns = listOf("\"","\""),
            ranges = listOf(IntRange(8, 8), IntRange(13, 13)),
            recognizer = recognizer,
            title = "String (quotation mark)"
        )
        val no7 = RecognizerMatch(
            patterns = listOf("[^\"]*"),
            ranges = listOf(IntRange(9, 12)),
            recognizer = recognizer,
            title = "String (quotation mark) content (no quotation mark)"
        )
        val no8 = RecognizerMatch(
            patterns = listOf("(?:[^\\\\\"]|\\\\\\\\|\\\\\")*"),
            ranges = listOf(IntRange(9, 12)),
            recognizer = recognizer,
            title = "String (quotation mark) content (backslash escaped quotation mark)"
        )
        val no9 = RecognizerMatch(
            patterns = listOf("(?:[^\"]|\"\")*"),
            ranges = listOf(IntRange(9, 12)),
            recognizer = recognizer,
            title = "String (quotation mark) content (double escaped quotation mark)"
        )
        assertMatchesEqual(expected = listOf(no1, no2, no3, no4, no5, no6, no7, no8, no9), actual = result)
    }
}
