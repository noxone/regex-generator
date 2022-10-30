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
        val s1 = S1UserInput(userInput = "aaa")

        // when
        val s2 = PatternRecognizerModel(input = s1.output, options = RecognizerCombiner.Options())

        //then
        assertEquals(expected = 9, actual = s2.matches.size)
    }

    @Test
    fun testPatterns_numberOfMatches2() {
        // given
        val s1 = S1UserInput(userInput = "TX_RESP_Q008")

        // when
        val s2 = PatternRecognizerModel(input = s1.output, options = RecognizerCombiner.Options())

        //then
        assertEquals(expected = 33, actual = s2.matches.size)
    }

    @Test
    fun testPatterns_simpleRecognition() {
        val s2 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())

        val titles = s2.matches.map { it.title }

        assertContains(titles, "Number")
        assertContains(titles, "Multiple characters")
    }

    @Suppress("MaxLineLength")
    @Test
    fun testPatterns_selectOneMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }

        // when
        val s2n2 = s2n1.select(numberMatch)

        // then
        assertTrue(s2n1.selectedMatches.isEmpty(), message = "Initial number of matches is zero")
        assertEquals(expected = 1, actual = s2n2.selectedMatches.size, message = "Should be exactly one match after selection")
        assertEquals(expected = "Number", actual = s2n2.selectedMatches.first().title)
    }

    @Test
    fun testPatterns_selectInvalidMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }
        val digitMatch = s2n1.matches.first { it.title == "Digit" }
        val s2n2 = s2n1.select(numberMatch)

        // when
        val s2n3 = s2n2.select(digitMatch)

        // then
        assertEquals(expected = s2n2.selectedMatches, actual = s2n3.selectedMatches)
    }

    @Test
    fun testPatterns_selectValidMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }
        val characterMatch = s2n1.matches.first { it.title == "One character" }
        val s2n2 = s2n1.select(numberMatch)

        // when
        val s2n3 = s2n2.select(characterMatch)

        // then
        assertTrue(s2n3.selectedMatches.size > s2n2.selectedMatches.size)
    }

    @Test
    fun testPatterns_deselectOneMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }
        val s2n2 = s2n1.select(numberMatch)

        // when
        val s2n3 = s2n2.deselect(s2n2.selectedMatches.first())

        // then
        assertTrue(s2n3.selectedMatches.isEmpty())
    }

    @Test
    fun testPatterns_simpleOutput() {
        val expected = "abc[0-9]+def"

        // given
        val s2n1 = PatternRecognizerModel(input = stage1abc123def.output, options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }

        // when
        val s2n2 = s2n1.select(numberMatch)

        // then
        assertEquals(expected = expected, actual = s2n2.output)
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersToSelectionMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = "abc123def", options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }
        val s2n2 = s2n1.select(numberMatch)

        // when
        val s2n3 = s2n2.setUserInput("abc1243def")

        // then
        assertEquals(expected = 1, actual = s2n3.selectedMatches.size, "Number of matches")
        val actualMatch = s2n3.selectedMatches.first()
        assertEquals(expected = "Number", actual = actualMatch.title, "Match title")
        assertEquals(expected = numberMatch.first, actual = actualMatch.first, "First index of match")
        assertEquals(expected = numberMatch.last + 1, actual = actualMatch.last, "Last index of match")
    }

    @Test
    fun shouldKeepTheSelectionWhenAddingCharactersPriorToSelectionMatch() {
        // given
        val s2n1 = PatternRecognizerModel(input = "abc123def", options = RecognizerCombiner.Options())
        val numberMatch = s2n1.matches.first { it.title == "Number" }
        val s2n2 = s2n1.select(numberMatch)

        // when
        val s2n3 = s2n2.setUserInput("abdc123def")

        // then
        assertEquals(expected = 1, actual = s2n3.selectedMatches.size, "Number of matches")
        val actualMatch = s2n3.selectedMatches.first()
        assertEquals(expected = "Number", actual = actualMatch.title, "Match title")
        assertEquals(expected = numberMatch.first + 1, actual = actualMatch.first, "First index of match")
        assertEquals(expected = numberMatch.last + 1, actual = actualMatch.last, "Last index of match")
    }

    /*@Test
    fun testModel_A() {
        val s1s1 = S1UserInput(userInput = "abc123")
        val s1s2 = s1s1.setUserInput(newInputString = "abc1234")

        val s2s1 = S2PatternRecognition(input = s1s1)
        val s2s2 = S2PatternRecognition(input = s1s2.model, previousMatches = s2s1.matches, inputChange = s1s2.change)

        console.log(s2s1.matches.org.olafneumann.regex.generator.util.toIndexedString("pr1: \n"))
        console.log(s2s2.matches.org.olafneumann.regex.generator.util.toIndexedString("pr2: \n"))
        console.log(s2s2.diffs.operations.org.olafneumann.regex.generator.util.toIndexedString("Operations:\n"))
    }

    @Test
    fun testModel_B() {
        val input1 = S1UserInput(userInput = "abc123")
        val input2 = S1UserInput(userInput = "abcd123")

        val pr1 = S2PatternRecognition(input = input1)
        val pr2 = S2PatternRecognition(input = input2, previousMatches = pr1.matches)

        console.log(pr1.matches.org.olafneumann.regex.generator.util.toIndexedString("pr1: \n"))
        console.log(pr2.matches.org.olafneumann.regex.generator.util.toIndexedString("pr2: \n"))
        console.log(pr2.diffs.operations.org.olafneumann.regex.generator.util.toIndexedString("Operations:\n"))
    }*/

    companion object {
        private val stage1abc123def = S1UserInput(userInput = "abc123def")
        fun List<Any>.toIndexedString(prefix: String = ""): String =
            this
                .mapIndexed { index, recognizerMatch -> index to recognizerMatch }
                .joinToString(separator = "\n", prefix = prefix) { "${it.first}: ${it.second}" }
    }
}
