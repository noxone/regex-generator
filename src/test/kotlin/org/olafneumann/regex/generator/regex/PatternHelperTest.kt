package org.olafneumann.regex.generator.regex

import kotlin.test.Test
import kotlin.test.assertEquals

class PatternHelperTest {
    companion object {
        private const val given = "abc.\\\$hier \"und\" / 'da'([)."
    }

    @Test
    fun testEscapeForRegex() {
        val expected = "abc\\.\\\\\\\$hier \"und\" / 'da'\\)\\[\\)\\."

        val actual = given.escapeForRegex()

        assertEquals(expected, actual)
    }
}