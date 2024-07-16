package org.olafneumann.regex.generator.recognizer

import kotlin.test.Test
import kotlin.test.assertEquals

class RecognizerTest {
    @Test
    fun findAllRecognizers() {
        val input = listOf(
            "978",
            ".345",
            "abcd",
            "aaa",
            "INFO",
            "%20",
            "12",
            "2022-12-10",
            "12:34:56",
            "2020-03-12T13:34:56.123Z",
            "127.0.0.1",
            "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
            "#hashtag",
            "#112233",
            "test@mail.com",
            "bla (blub)",
            "e{ve}n",
            "[mor]",
            "St\"ri\"ng",
            "St'ir'ng"
        )
            .joinToString(separator = " ")

        val foundRecognizerNames = RecognizerRegistry.findMatches(input)
            .map { it.recognizer.name }

        // make sure, each pattern is found at least once
        val notFoundRecognizerNames = RecognizerRegistry.allRecognizerNames
            .filter { !foundRecognizerNames.contains(it) }

        assertEquals(
            expected = emptyList(),
            actual = notFoundRecognizerNames,
            message = "These recognizers have not been found"
        )
    }
}
