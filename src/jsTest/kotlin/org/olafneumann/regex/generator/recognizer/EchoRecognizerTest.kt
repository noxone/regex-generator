package org.olafneumann.regex.generator.recognizer

import org.olafneumann.regex.generator.recognizer.RecognizersTestHelper.assertMatchesEqual
import kotlin.test.Test

class EchoRecognizerTest {
    @Test
    fun testEchoRecognizer() {
        val given = "abc123def4,5ghi67jkl8mno"
        val recognizer = EchoRecognizer("Exact number", "[0-9]{2,}")

        val result = recognizer.findMatches(given)

        val no1 = RecognizerMatch(
            patterns = listOf("123"),
            ranges = listOf(IntRange(3, 5)),
            recognizer = recognizer,
            title = "Exact number (123)"
        )
        val no2 = RecognizerMatch(
            patterns = listOf("67"),
            ranges = listOf(IntRange(15, 16)),
            recognizer = recognizer,
            title = "Exact number (67)"
        )
        assertMatchesEqual(expected = listOf(no1, no2), actual = result)
    }
}
