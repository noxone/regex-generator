package org.olafneumann.regex.generator.model

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class S2PatternRecognitionTest {
    @Test
    fun testPatterns_numberOfMatches1() {
        // given
        val s1 = S1UserInput(userInput = "aaa")

        // when
        val s2 = S2PatternRecognition(input = s1)

        //then
        assertEquals(expected = 9, actual = s2.matches.size)
    }

    @Test
    fun testPatterns_numberOfMatches2() {
        // given
        val s1 = S1UserInput(userInput = "TX_RESP_Q008")

        // when
        val s2 = S2PatternRecognition(input = s1)

        //then
        assertEquals(expected = 33, actual = s2.matches.size)
    }

    @Test
    fun testPatterns_simpleRecognition() {
        val s1 = S1UserInput(userInput = "abc123def")
        val s2 = S2PatternRecognition(input = s1)

        val titles = s2.matches.map { it.title }

        assertContains(titles, "Number")
        assertContains(titles, "Multiple characters")
    }

    @Test
    fun testModel_A() {
        val s1s1 = S1UserInput(userInput = "abc123")
        val s1s2 = s1s1.setUserInput(newInputString = "abc1234")

        val s2s1 = S2PatternRecognition(input = s1s1)
        val s2s2 = S2PatternRecognition(input = s1s2.model, previousMatches = s2s1.matches, inputChange = s1s2.change)

        console.log(s2s1.matches.toIndexedString("pr1: \n"))
        console.log(s2s2.matches.toIndexedString("pr2: \n"))
        console.log(s2s2.diffs.operations.toIndexedString("Operations:\n"))
    }

    @Test
    fun testModel_B() {
        val input1 = S1UserInput(userInput = "abc123")
        val input2 = S1UserInput(userInput = "abcd123")

        val pr1 = S2PatternRecognition(input = input1)
        val pr2 = S2PatternRecognition(input = input2, previousMatches = pr1.matches)

        console.log(pr1.matches.toIndexedString("pr1: \n"))
        console.log(pr2.matches.toIndexedString("pr2: \n"))
        console.log(pr2.diffs.operations.toIndexedString("Operations:\n"))
    }

    companion object {
        fun List<Any>.toIndexedString(prefix: String = ""): String =
            this
                .mapIndexed { index, recognizerMatch -> index to recognizerMatch }
                .joinToString(separator = "\n", prefix = prefix) { "${it.first}: ${it.second}" }
    }
}