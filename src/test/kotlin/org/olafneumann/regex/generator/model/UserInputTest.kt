package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserInputTest {

    @Test
    fun testModel_List() {
        val given = "abc"
        val input = S1UserInput(userInput = given)

        assertEquals(expected = given, actual = input.userString)
        assertEquals(3, actual = input.size)
    }


    // make sure, the 3rd party lib behaves the same...
    @Test
    fun testDifference_AddLetter() {
        val input = S1UserInput(userInput = "abce")

        val result = input.setUserInput(newInputString = "abcde")
        val changes = result.change.changes

        val out = changes.joinToString { it.toString() }
        console.log(out)

        assertEquals(1 , changes.size)
        assertTrue { changes[0] is DiffOperation.Add }

        val add = changes[0] as DiffOperation.Add
        assertEquals(3, add.index)
        assertEquals('d', add.item)
    }

    @Test
    fun testDifference_RemoveLetter() {
        val input = S1UserInput(userInput = "abcdef")

        val result = input.setUserInput(newInputString = "acdf")
        val changes = result.change.changes

        val out = changes.joinToString { it.toString() }
        console.log(out)

        assertEquals(2 , changes.size)
        assertTrue { changes[0] is DiffOperation.Remove }

        val remove1 = changes[0] as DiffOperation.Remove
        assertEquals(1, remove1.index)
        assertEquals('b', remove1.item)

        val remove2 = changes[1] as DiffOperation.Remove
        assertEquals(3, remove2.index)
        assertEquals('e', remove2.item)
    }


    @Test
    fun testDifference_ExchangeText() {
        val input = S1UserInput(userInput = "morning")

        val result = input.setUserInput(newInputString = "bookshelf")
        val changes = result.change.changes

        val out = changes.joinToString { it.toString() }
        console.log(out)

        assertEquals(4 , changes.size)
    }
}
