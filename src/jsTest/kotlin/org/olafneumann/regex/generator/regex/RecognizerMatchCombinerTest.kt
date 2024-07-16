package org.olafneumann.regex.generator.regex

import org.olafneumann.regex.generator.recognizer.EchoRecognizer
import org.olafneumann.regex.generator.recognizer.RecognizerMatch
import kotlin.test.Test
import kotlin.test.assertEquals

class RecognizerMatchCombinerTest {
    companion object {
        private val recognizer = EchoRecognizer("dummy", "dummy")
        private const val inputText = "abc123abc"
        private val selectedMatches = listOf(
            RecognizerMatch(
                patterns = listOf("[a-z]+"),
                ranges = listOf(IntRange(0, 2)),
                recognizer = recognizer,
                title = "dummy"
            ),
            RecognizerMatch(
                patterns = listOf("[a-z]+"),
                ranges = listOf(IntRange(6, 8)),
                recognizer = recognizer,
                title = "dummy"
            )
        )
    }

    @Test
    fun testMatchCombinationWithoutOnlyPatterns() {
        val expected = "[a-z]+123[a-z]+"

        val actual = RecognizerMatchCombiner.combineMatches(
            inputText = inputText,
            selectedMatches = selectedMatches,
            options = RecognizerMatchCombinerOptions(onlyPatterns = false, generateLowerCase = true)
        ).pattern

        assertEquals(expected = expected, actual = actual)
    }

    @Test
    fun testMatchCombinationWithOnlyPatterns() {
        val expected = "[A-Za-z]+.*[A-Za-z]+"

        val actual = RecognizerMatchCombiner.combineMatches(
            inputText = inputText,
            selectedMatches = selectedMatches,
            options = RecognizerMatchCombinerOptions(onlyPatterns = true, generateLowerCase = false)
        ).pattern

        assertEquals(expected = expected, actual = actual)
    }
}
