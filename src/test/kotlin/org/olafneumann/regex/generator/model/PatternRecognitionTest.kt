package org.olafneumann.regex.generator.model

import kotlin.test.Test

class PatternRecognitionTest {
    @Test
    fun testModel_A() {
        val input1 = UserInput(userInput = "abc123")
        val input2 = input1.setUserInput(newInputString = "abc1234")

        val pr1 = PatternRecognition(input = input1)
        val pr2 = PatternRecognition(input = input2.model, previousMatches = pr1.matches, change = input2.change)

        console.log(pr1.matches.toIndexedString("pr1: \n"))
        console.log(pr2.matches.toIndexedString("pr2: \n"))
        console.log(pr2.diffs.operations.toIndexedString("Operations:\n"))
    }

    @Test
    fun testModel_B() {
        val input1 = UserInput(userInput = "abc123")
        val input2 = UserInput(userInput = "abcd123")

        val pr1 = PatternRecognition(input = input1)
        val pr2 = PatternRecognition(input = input2, previousMatches = pr1.matches)

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