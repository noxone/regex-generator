package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.recognizer.RecognizersTestHelper.assertMatchesEqual
import kotlin.test.Test

class SimpleRecognizerTest {
    @Test
    fun testSimpleRecognizer() {
        val given = "abc123def4,5ghi"
        val recognizer = SimpleRecognizer("Number", "\\d+")

        val result = recognizer.findMatches(given)

        val no1 = RecognizerMatch(
            patterns = listOf("\\d+"),
            ranges = listOf(IntRange(3, 5)),
            recognizer = recognizer,
            title = "Number"
        )
        val no2 = RecognizerMatch(
            patterns = listOf("\\d+"),
            ranges = listOf(IntRange(9, 9)),
            recognizer = recognizer,
            title = "Number"
        )
        val no3 = RecognizerMatch(
            patterns = listOf("\\d+"),
            ranges = listOf(IntRange(11, 11)),
            recognizer = recognizer,
            title = "Number"
        )
        assertMatchesEqual(expected = listOf(no1, no2, no3), actual = result)
    }
}
