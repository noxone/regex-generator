package org.olafneumann.regex.generator.recognizer

import kotlin.test.Test
import kotlin.test.assertEquals

class PatternHelperTest {
    @Test
    fun testEscapeForRegex() {
        // original: abc.\$hier "und" / 'da'([).
        val given = """abc.\${'$'}hier "und" / 'da'([)."""
        // expected: abc\.\\\$hier "und" / 'da'\(\[\)\.
        val expected = """abc\.\\\${'$'}hier "und" / 'da'\(\[\)\."""

        val actual = given.escapeForRegex()

        assertEquals(expected, actual)
    }
}
