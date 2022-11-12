package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RegexMatchCombiner
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PatternRecognizerModelTest {
    @Test
    fun testPatterns_numberOfMatches1() {
        // given
        val input = "aaa"

        // when
        val model = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())

        //then
        assertEquals(expected = 9, actual = model.recognizerMatches.size)
    }

    @Test
    fun testPatterns_numberOfMatches2() {
        // given
        val input = "TX_RESP_Q008"

        // when
        val model = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())

        //then
        assertEquals(expected = 33, actual = model.recognizerMatches.size)
    }

    @Test
    fun testPatterns_simpleRecognition() {
        val input = "abc123def"
        val model = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())

        val titles = model.recognizerMatches.map { it.title }

        assertContains(titles, "Number")
        assertContains(titles, "Multiple characters")
    }

    @Suppress("MaxLineLength")
    @Test
    fun testPatterns_selectOneMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())
        val numberMatch = model1.recognizerMatches.first { it.title == "Number" }

        // when
        val model2 = model1.select(numberMatch)

        // then
        assertTrue(model1.selectedRecognizerMatches.isEmpty(), message = "Initial number of matches is zero")
        assertEquals(expected = 1, actual = model2.selectedRecognizerMatches.size, message = "Should be exactly one match after selection")
        assertEquals(expected = "Number", actual = model2.selectedRecognizerMatches.first().title)
    }

    @Test
    fun testPatterns_selectInvalidMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())
        val numberMatch = model1.recognizerMatches.first { it.title == "Number" }
        val digitMatch = model1.recognizerMatches.first { it.title == "Digit" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.select(digitMatch)

        // then
        assertEquals(expected = model2.selectedRecognizerMatches, actual = model3.selectedRecognizerMatches)
    }

    @Test
    fun testPatterns_selectValidMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())
        val numberMatch = model1.recognizerMatches.first { it.title == "Number" }
        val characterMatch = model1.recognizerMatches.first { it.title == "One character" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.select(characterMatch)

        // then
        assertTrue(model3.selectedRecognizerMatches.size > model2.selectedRecognizerMatches.size)
    }

    @Test
    fun testPatterns_deselectOneMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())
        val numberMatch = model1.recognizerMatches.first { it.title == "Number" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.deselect(model2.selectedRecognizerMatches.first())

        // then
        assertTrue(model3.selectedRecognizerMatches.isEmpty())
    }

    @Test
    fun testPatterns_simpleOutput() {
        val expected = "abc[0-9]+def"

        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RegexMatchCombiner.Options())
        val numberMatch = model1.recognizerMatches.first { it.title == "Number" }

        // when
        val model2 = model1.select(numberMatch)

        // then
        assertEquals(expected = expected, actual = model2.regularExpression.pattern)
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersToSelectionMatch() {
        shouldKeepTheSelectionWhenChangingInput(
            firstInput = "abc123def",
            secondInput = "abc1243def",
            recognizerName = "Number",
            expectedFirst = 3,
            expectedLast = 6
        )
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersPriorToSelectionMatch() {
        shouldKeepTheSelectionWhenChangingInput(
            firstInput = "abc123def",
            secondInput = "abdc123def",
            recognizerName = "Number",
            expectedFirst = 4,
            expectedLast = 6
        )
    }

    @Test
    fun testKeepSelection1() {
        shouldKeepTheSelectionWhenChangingInput(
            firstInput = "asd1231sdf",
            secondInput = "asd11sdf",
            recognizerName = "Number",
            expectedFirst = 3,
            expectedLast = 4
        )
    }

    @Test
    fun testKeepSelection2() {
        shouldKeepTheSelectionWhenChangingInput(
            firstInput = "s",
            secondInput = "sdf",
            recognizerName = "Multiple characters",
            expectedFirst = 0,
            expectedLast = 2
        )
    }

    @Test
    fun testKeepSelection3() {
        shouldKeepTheSelectionWhenChangingInput(
            firstInput = "a(sd)f",
            secondInput = "a(serd)f",
            recognizerName = "Parentheses",
            expectedFirst = 1,
            expectedLast = 6
        )
    }

    private fun shouldKeepTheSelectionWhenChangingInput(
        firstInput: String,
        recognizerName: String,
        secondInput: String,
        expectedFirst: Int,
        expectedLast: Int
    ) {
        // given
        val model1 = PatternRecognizerModel(input = firstInput, options = RegexMatchCombiner.Options())
        val match = model1.recognizerMatches.first { it.title == recognizerName }
        val model2 = model1.select(match)

        // when
        val model3 = model2.setUserInput(secondInput)

        // then
        assertEquals(expected = 1, actual = model3.selectedRecognizerMatches.size, "Number of matches")
        val actualMatch = model3.selectedRecognizerMatches.first()
        assertEquals(expected = recognizerName, actual = actualMatch.title, "Match title")
        assertEquals(expected = expectedFirst, actual = actualMatch.first, "First index of match")
        assertEquals(expected = expectedLast, actual = actualMatch.last, "Last index of match")
    }

    companion object {
        fun List<Any>.toIndexedString(prefix: String = ""): String =
            this
                .mapIndexed { index, recognizerMatch -> index to recognizerMatch }
                .joinToString(separator = "\n", prefix = prefix) { "${it.first}: ${it.second}" }
    }
}
