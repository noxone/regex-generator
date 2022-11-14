import org.olafneumann.regex.generator.regex.Options
import org.olafneumann.regex.generator.regex.RegexMatchCombiner
import kotlin.test.Test
import kotlin.test.assertEquals

class RegexGenerationTest {
    companion object {
        private fun assertRegexIsCorrect(given: String, expected: String, options: Options = Options()) {
            val actual =
                RegexMatchCombiner.combineMatches(inputText = given, selectedMatches = emptyList(), options = options)

            assertEquals(expected = expected, actual = actual.finalPattern)
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
