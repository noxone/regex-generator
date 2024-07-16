package org.olafneumann.regex.generator.recognizer

import kotlin.test.assertEquals
import kotlin.test.assertSame

object RecognizersTestHelper {
    private fun assertMatchEquals(expected: RecognizerMatch, actual: RecognizerMatch) {
        assertEquals(expected.patterns, actual.patterns, "Patterns")
        assertEquals(expected.ranges, actual.ranges, "Ranges")
        assertSame(expected.recognizer, actual.recognizer, "Recognizer")
        assertEquals(expected.title, actual.title, "Title")
    }

    fun assertMatchesEqual(expected: Collection<RecognizerMatch>, actual: Collection<RecognizerMatch>) {
        assertEquals(expected.size, actual.size, message = "Number of RecognizerMatches")
        expected.zip(actual)
            .forEach { assertMatchEquals(expected = it.first, actual = it.second) }
    }
}
