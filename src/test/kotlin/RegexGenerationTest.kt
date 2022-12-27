import org.olafneumann.regex.generator.regex.RecognizerMatchCombiner
import kotlin.test.Test
import kotlin.test.assertEquals

class RegexGenerationTest {
    companion object {
        private fun assertRegexIsCorrect(given: String, expected: String) {
            val actual =
                RecognizerMatchCombiner.combineMatches(
                    inputText = given,
                    selectedMatches = emptyList()
                )

            assertEquals(expected = expected, actual = actual.pattern)
        }
    }

    @Test
    fun testRegex01() {
        val given = "abc["
        val expected = "abc\\["

        assertRegexIsCorrect(given = given, expected = expected)
    }

    @Test
    fun testRegex02() {
        val given = "| | | 13024a.htm"
        val expected = "\\| \\| \\| 13024a\\.htm"

        assertRegexIsCorrect(given = given, expected = expected)
    }
}
