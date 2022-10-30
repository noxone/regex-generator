package org.olafneumann.regex.generator.model

import org.olafneumann.regex.generator.regex.RecognizerCombiner
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
        val model = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())

        //then
        assertEquals(expected = 9, actual = model.matches.size)
    }

    @Test
    fun testPatterns_numberOfMatches2() {
        // given
        val input = "TX_RESP_Q008"

        // when
        val model = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())

        //then
        assertEquals(expected = 33, actual = model.matches.size)
    }

    @Test
    fun testPatterns_simpleRecognition() {
        val input = "abc123def"
        val model = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())

        val titles = model.matches.map { it.title }

        assertContains(titles, "Number")
        assertContains(titles, "Multiple characters")
    }

    @Suppress("MaxLineLength")
    @Test
    fun testPatterns_selectOneMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }

        // when
        val model2 = model1.select(numberMatch)

        // then
        assertTrue(model1.selectedMatches.isEmpty(), message = "Initial number of matches is zero")
        assertEquals(expected = 1, actual = model2.selectedMatches.size, message = "Should be exactly one match after selection")
        assertEquals(expected = "Number", actual = model2.selectedMatches.first().title)
    }

    @Test
    fun testPatterns_selectInvalidMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }
        val digitMatch = model1.matches.first { it.title == "Digit" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.select(digitMatch)

        // then
        assertEquals(expected = model2.selectedMatches, actual = model3.selectedMatches)
    }

    @Test
    fun testPatterns_selectValidMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }
        val characterMatch = model1.matches.first { it.title == "One character" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.select(characterMatch)

        // then
        assertTrue(model3.selectedMatches.size > model2.selectedMatches.size)
    }

    @Test
    fun testPatterns_deselectOneMatch() {
        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.deselect(model2.selectedMatches.first())

        // then
        assertTrue(model3.selectedMatches.isEmpty())
    }

    @Test
    fun testPatterns_simpleOutput() {
        val expected = "abc[0-9]+def"

        // given
        val input = "abc123def"
        val model1 = PatternRecognizerModel(input = input, options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }

        // when
        val model2 = model1.select(numberMatch)

        // then
        assertEquals(expected = expected, actual = model2.regularExpression)
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersToSelectionMatch() {
        // given
        val model1 = PatternRecognizerModel(input = "abc123def", options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.setUserInput("abc1243def")

        // then
        assertEquals(expected = 1, actual = model3.selectedMatches.size, "Number of matches")
        val actualMatch = model3.selectedMatches.first()
        assertEquals(expected = "Number", actual = actualMatch.title, "Match title")
        assertEquals(expected = numberMatch.first, actual = actualMatch.first, "First index of match")
        assertEquals(expected = numberMatch.last + 1, actual = actualMatch.last, "Last index of match")
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersPriorToSelectionMatch() {
        // given
        val model1 = PatternRecognizerModel(input = "abc123def", options = RecognizerCombiner.Options())
        val numberMatch = model1.matches.first { it.title == "Number" }
        val model2 = model1.select(numberMatch)

        // when
        val model3 = model2.setUserInput("abdc123def")

        // then
        assertEquals(expected = 1, actual = model3.selectedMatches.size, "Number of matches")
        val actualMatch = model3.selectedMatches.first()
        assertEquals(expected = "Number", actual = actualMatch.title, "Match title")
        assertEquals(expected = numberMatch.first + 1, actual = actualMatch.first, "First index of match")
        assertEquals(expected = numberMatch.last + 1, actual = actualMatch.last, "Last index of match")
    }

    companion object {
        fun List<Any>.toIndexedString(prefix: String = ""): String =
            this
                .mapIndexed { index, recognizerMatch -> index to recognizerMatch }
                .joinToString(separator = "\n", prefix = prefix) { "${it.first}: ${it.second}" }
    }
}
